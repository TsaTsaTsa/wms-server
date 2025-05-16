-- 01-create-schema.sql
-- Схема таблицы tiles: последовательность + таблица

-- создаём последовательность для первичных ключей
-- 00-create-sequence.sql

CREATE EXTENSION IF NOT EXISTS postgis;

CREATE SEQUENCE IF NOT EXISTS tiles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- 01-create-schema.sql
CREATE TABLE IF NOT EXISTS public.tiles
(
    id integer NOT NULL DEFAULT nextval('tiles_id_seq'::regclass),
    tile_id VARCHAR(255) UNIQUE,
    bounding_box geometry(Polygon,4326),
    shard_id VARCHAR(255),
    CONSTRAINT tiles_pkey PRIMARY KEY (id)
)
TABLESPACE pg_default;

ALTER TABLE public.tiles OWNER TO postgres;
