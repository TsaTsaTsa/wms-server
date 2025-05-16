-- 00-create-user.sql
-- Создаём роль и БД с заданными параметрами

-- роль postgres с паролем 1234
DO
$$
BEGIN
   IF NOT EXISTS (
       SELECT FROM pg_catalog.pg_roles WHERE rolname = 'postgres'
   ) THEN
       CREATE ROLE postgres WITH LOGIN PASSWORD '1234';
   END IF;
END
$$;

-- создаём базу tiles_metadata, если ещё нет
SELECT
   CASE
     WHEN NOT EXISTS (
       SELECT FROM pg_database WHERE datname = 'tiles_metadata'
     )
     THEN
       pg_catalog.pg_create_db('tiles_metadata')
     ELSE
       NULL
   END;

SELECT
   CASE
     WHEN NOT EXISTS (
       SELECT FROM pg_database WHERE datname = 'wms_metadata'
     )
     THEN
       pg_catalog.pg_create_db('wms_metadata')
     ELSE
       NULL
   END;
