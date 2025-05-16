-- 02-load-data.sql
-- Здесь приводятся INSERT’ы или COPY-операции с вашими данными.
-- Пример:
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_0_0', ST_MakeEnvelope(3345390.101371, 1521522.495849, 3662040.692753, 1837828.258099, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_0_1', ST_MakeEnvelope(3345390.101371, 1205216.733598, 3662040.692753, 1521522.495849, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_0_2', ST_MakeEnvelope(3345390.101371, 888910.971348, 3662040.692753, 1205216.733598, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_0_3', ST_MakeEnvelope(3345390.101371, 572605.209098, 3662040.692753, 888910.971348, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_0_4', ST_MakeEnvelope(3345390.101371, 330813.633015, 3662040.692753, 572605.209098, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_1_0', ST_MakeEnvelope(3662040.692753, 1521522.495849, 3978691.284134, 1837828.258099, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_1_1', ST_MakeEnvelope(3662040.692753, 1205216.733598, 3978691.284134, 1521522.495849, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_1_2', ST_MakeEnvelope(3662040.692753, 888910.971348, 3978691.284134, 1205216.733598, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_1_3', ST_MakeEnvelope(3662040.692753, 572605.209098, 3978691.284134, 888910.971348, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_1_4', ST_MakeEnvelope(3662040.692753, 330813.633015, 3978691.284134, 572605.209098, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_2_0', ST_MakeEnvelope(3978691.284134, 1521522.495849, 4295341.875515, 1837828.258099, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_2_1', ST_MakeEnvelope(3978691.284134, 1205216.733598, 4295341.875515, 1521522.495849, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_2_2', ST_MakeEnvelope(3978691.284134, 888910.971348, 4295341.875515, 1205216.733598, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_2_3', ST_MakeEnvelope(3978691.284134, 572605.209098, 4295341.875515, 888910.971348, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_2_4', ST_MakeEnvelope(3978691.284134, 330813.633015, 4295341.875515, 572605.209098, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_3_0', ST_MakeEnvelope(4295341.875515, 1521522.495849, 4611992.466897, 1837828.258099, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_3_1', ST_MakeEnvelope(4295341.875515, 1205216.733598, 4611992.466897, 1521522.495849, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_3_2', ST_MakeEnvelope(4295341.875515, 888910.971348, 4611992.466897, 1205216.733598, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_3_3', ST_MakeEnvelope(4295341.875515, 572605.209098, 4611992.466897, 888910.971348, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_3_4', ST_MakeEnvelope(4295341.875515, 330813.633015, 4611992.466897, 572605.209098, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_4_0', ST_MakeEnvelope(4611992.466897, 1521522.495849, 4928643.058278, 1837828.258099, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_4_1', ST_MakeEnvelope(4611992.466897, 1205216.733598, 4928643.058278, 1521522.495849, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_4_2', ST_MakeEnvelope(4611992.466897, 888910.971348, 4928643.058278, 1205216.733598, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_4_3', ST_MakeEnvelope(4611992.466897, 572605.209098, 4928643.058278, 888910.971348, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_4_4', ST_MakeEnvelope(4611992.466897, 330813.633015, 4928643.058278, 572605.209098, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_5_0', ST_MakeEnvelope(4928643.058278, 1521522.495849, 5158413.415408, 1837828.258099, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_5_1', ST_MakeEnvelope(4928643.058278, 1205216.733598, 5158413.415408, 1521522.495849, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_5_2', ST_MakeEnvelope(4928643.058278, 888910.971348, 5158413.415408, 1205216.733598, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_5_3', ST_MakeEnvelope(4928643.058278, 572605.209098, 5158413.415408, 888910.971348, 0), 1);
INSERT INTO tiles(tile_id, bounding_box, shard_id) VALUES ('tile_5_4', ST_MakeEnvelope(4928643.058278, 330813.633015, 5158413.415408, 572605.209098, 0), 1);