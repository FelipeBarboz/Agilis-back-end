-- Adiciona coluna attendance_type em store_units
ALTER TABLE store_units
    ADD COLUMN IF NOT EXISTS attendance_type VARCHAR(20) NOT NULL DEFAULT 'FIXED_LOCATION';

-- Torna os campos de endereço nullable (obrigatório apenas para FIXED_LOCATION e BOTH via domínio)
ALTER TABLE store_units
    ALTER COLUMN street     DROP NOT NULL,
    ALTER COLUMN number     DROP NOT NULL,
    ALTER COLUMN city       DROP NOT NULL,
    ALTER COLUMN state      DROP NOT NULL,
    ALTER COLUMN cep        DROP NOT NULL;
