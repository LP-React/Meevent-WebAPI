INSERT INTO cities (city_name, country_id) VALUES
-- Lima
('Lima', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Callao', (SELECT country_id FROM countries WHERE iso_code = 'PER')),

-- Costa
('Trujillo', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Chiclayo', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Piura', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Tumbes', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Chimbote', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Ica', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Pisco', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Nazca', (SELECT country_id FROM countries WHERE iso_code = 'PER')),

-- Sierra
('Cusco', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Arequipa', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Puno', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Huancayo', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Ayacucho', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Cajamarca', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Huaraz', (SELECT country_id FROM countries WHERE iso_code = 'PER')),

-- Selva
('Iquitos', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Pucallpa', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Tarapoto', (SELECT country_id FROM countries WHERE iso_code = 'PER')),
('Puerto Maldonado', (SELECT country_id FROM countries WHERE iso_code = 'PER'));
