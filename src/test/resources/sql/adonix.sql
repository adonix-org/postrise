DROP TABLE IF EXISTS public.test;

CREATE TABLE IF NOT EXISTS public.test
(
    id SERIAL,
    value text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT test_pkey PRIMARY KEY (id)
);
