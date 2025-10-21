INSERT INTO iban_accounts (iban)
SELECT
    'BG' || LPAD((10 + (random()*89)::int)::text, 2, '0') ||
    'BANK' ||
    LPAD((floor(random()*9999))::text, 4, '0') ||
    LPAD((floor(random()*9999999999))::text, 10, '0')
FROM generate_series(1, 1000000);