ALTER TABLE comment
ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT true;