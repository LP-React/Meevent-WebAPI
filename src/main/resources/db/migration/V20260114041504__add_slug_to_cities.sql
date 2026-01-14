-- =====================================================
-- Add slug field to cities to support SEO-friendly URLs,
-- routing and filtering by readable identifiers.
--
-- DESIGN:
-- - slug is lowercase and URL-safe
-- - uniqueness is enforced per country
-- =====================================================

-- Add slug column
ALTER TABLE cities
ADD COLUMN city_slug VARCHAR(120);

-- Temporary default value for existing records
-- (avoids NOT NULL violation during migration)
UPDATE cities
SET city_slug = LOWER(REPLACE(city_name, ' ', '-'))
WHERE city_slug IS NULL;

-- Enforce NOT NULL after data migration
ALTER TABLE cities
ALTER COLUMN city_slug SET NOT NULL;

-- Enforce uniqueness per country
ALTER TABLE cities
ADD CONSTRAINT uk_cities_country_slug UNIQUE (country_id, city_slug);
