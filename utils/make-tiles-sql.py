#!/usr/bin/env python
"""
make-tiles-sql.py

Создаёт файл с INSERT-запросами для таблицы tiles.
Параметры передаются через CLI-флаги (все имеют значения по умолчанию):

  --dir        каталог с GeoTIFF-файлами      [tiles/]
  --out        выходной .sql-файл             [tiles.sql]
  --shard-id   числовой идентификатор шарда   [1]
  --table      имя таблицы                    [tiles]
  --geom-col   имя гео-колонки                [bounding_box]

Пример:

  python make-tiles-sql.py \
         --dir ethiopia_128 \
         --out tiles_shard2.sql \
         --shard-id 2
"""
import argparse
from pathlib import Path

import rasterio
from rasterio.crs import CRS


def parse_args() -> argparse.Namespace:
    p = argparse.ArgumentParser()
    p.add_argument("--dir",      default="tiles",        help="directory with TIFFs")
    p.add_argument("--out",      default="tiles.sql",    help="output .sql file")
    p.add_argument("--shard-id", default=1, type=int,    help="target shard_id")
    p.add_argument("--table",    default="tiles",        help="table name")
    p.add_argument("--geom-col", default="bounding_box", help="geometry column")
    return p.parse_args()


def main() -> None:
    args = parse_args()
    src_dir  = Path(args.dir)
    out_path = Path(args.out)

    rows: list[str] = []
    for tif in sorted(src_dir.glob("*.tif")):
        with rasterio.open(tif) as ds:
            b = ds.bounds
            crs: CRS = ds.crs
            srid = crs.to_epsg() or 0

        rows.append(
            f"INSERT INTO {args.table}(tile_id, {args.geom_col}, shard_id) VALUES ("
            f"'{tif.stem}', "
            f"ST_MakeEnvelope({b.left:.6f}, {b.bottom:.6f}, "
            f"{b.right:.6f}, {b.top:.6f}, {srid}), {args.shard_id});"
        )

    out_path.write_text("\n".join(rows), encoding="utf-8")
    print(f"✓ {len(rows)} rows written to {out_path}")


if __name__ == "__main__":
    main()
