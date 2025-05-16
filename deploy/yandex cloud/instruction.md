## Облачный запуск
Инструкция повторяет логику локального Docker Compose, но переносит контейнеры на отдельные виртуальные машины. Все команды выполняются через `yc cli`.

---
### 1 Предварительные требования

| Инструмент | Миним. версия | Ссылка |
|------------|--------------|--------|
| `yc cli`   | 0.112+ | <https://cloud.yandex.ru/docs/cli>|

Аппаратные требования для каждой рабочей ВМ: 4 vCPU · 8 ГБ RAM · 50 ГБ SSD.

---

### 2 Сеть и NAT-шлюз
```
yc vpc network  create --name wms-net
yc vpc subnet   create --name wms-subnet \
  --zone ru-central1-a --range 10.10.0.0/24 --network-name wms-net

yc vpc gateway  create --name wms-gw --type internet
yc vpc route-table create --name wms-rt --network-name wms-net \
  --static-route address-prefix=0.0.0.0/0,gateway-id=$(yc vpc gateway get wms-gw --format=json | jq -r .id)

yc vpc subnet update --name wms-subnet --route-table-name wms-rt
```

### 3 Security Groups
```
# публичный HTTP/gRPC-трафик
yc vpc security-group create --name sg-public --network-name wms-net \
  --rule ingress protocol=tcp,port=80-8080,v4-cidr=0.0.0.0/0

# доступ к PostGIS только из VPC
yc vpc security-group create --name sg-db --network-name wms-net \
  --rule ingress protocol=tcp,port=5432,v4-cidr=10.10.0.0/24
```
### 4 Виртуальные машины-контейнеры
(зона ru-central1-a, все диски — network-SSD 50 ГБ)

**PostGIS**
```
yc compute instance create-with-container \
  --name postgis-vm \
  --zone ru-central1-a \
  --cores 4 --memory 8 \
  --disk type=network-ssd,size=50 \
  --network-interface subnet-name=wms-subnet,nat-ip-version=ipv4 \
  --security-group-name sg-db \
  --container-image postgis/postgis:17-3.5 \
  --container-env POSTGRES_USER=postgres,POSTGRES_PASSWORD=1234 \
  --container-privileged
```

**Tile-storage (2 реплики)**
```
for i in 0 1; do
  yc compute instance create-with-container \
    --name tile-storage-$i \
    --zone ru-central1-a \
    --cores 4 --memory 8 \
    --disk type=network-ssd,size=50,device-name=data \
    --network-interface subnet-name=wms-subnet,nat-ip-version=ipv4 \
    --security-group-name sg-public \
    --container-image cr.yandex/<folder-id>/wms-reg/tile-storage:1.0 \
    --container-env TILES_DIR=/data/tiles,STYLE_DIR=/data/styles \
    --container-mount src=data,target=/data \
    --container-port 50051
done
```
Загрузите каталоги tiles и styles на /data (SSH + rsync, либо Object Storage → curl).

**Nginx-шлюз**
```
yc compute instance create-with-container \
  --name nginx-vm \
  --zone ru-central1-a \
  --cores 2 --memory 2 \
  --network-interface subnet-name=wms-subnet,nat-ip-version=ipv4 \
  --security-group-name sg-public \
  --container-image nginx:1.23-alpine \
  --container-volume src=/local/nginx.conf,target=/etc/nginx/nginx.conf \
  --container-command nginx --container-args="-g,daemon off;" \
  --container-port 50051 --container-port 50052
```

**Central request-server**
```
yc compute instance create-with-container \
  --name wms-request-vm \
  --zone ru-central1-a \
  --cores 4 --memory 8 \
  --network-interface subnet-name=wms-subnet,nat-ip-version=ipv4 \
  --security-group-name sg-public \
  --container-image cr.yandex/<folder-id>/wms-reg/wms-request:1.0 \
  --container-env \
     NGINX_GATEWAY_HOST=nginx-vm, \
     TILE_DB_URL=jdbc:postgresql://postgis-vm:5432/tiles_metadata, \
     TILE_DB_USER=postgres, \
     TILE_DB_PASSWORD=1234 \
  --container-port 8080
```
Публичный IP этой ВМ используют клиенты WMS.
