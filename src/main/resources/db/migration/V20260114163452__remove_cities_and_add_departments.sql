DELETE FROM cities;
ALTER SEQUENCE cities_city_id_seq RESTART WITH 1;
INSERT INTO cities (city_name, country_id, city_slug) VALUES
-- Departamentos de Perú
('Lima', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'lima'),
('Arequipa', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'arequipa'),
('Cusco', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'cusco'),
('Piura', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'piura'),
('La Libertad', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'la-libertad'),
('Loreto', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'loreto'),
('San Martín', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'san-martin'),
('Ucayali', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'ucayali'),
('Puno', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'puno'),
('Tacna', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'tacna'),
('Moquegua', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'moquegua'),
('Apurímac', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'apurimac'),
('Ayacucho', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'ayacucho'),
('Huancavelica', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'huancavelica'),
('Junín', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'junin'),
('Huánuco', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'huanuco'),
('Cajamarca', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'cajamarca'),
('Amazonas', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'amazonas'),
('Ica', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'ica'),
('Callao', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'callao'),
('Tumbes', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'tumbes'),
('Ancash', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'ancash'),
('Pasco', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'pasco'),
('Madre de Dios', (SELECT country_id FROM countries WHERE iso_code = 'PER'), 'madre-de-dios');