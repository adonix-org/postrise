/*
 * Copyright (C) 2025 Ty Busby
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

DROP DATABASE IF EXISTS database_beta;
CREATE DATABASE database_beta
    WITH
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

DROP ROLE IF EXISTS beta_login;
CREATE ROLE beta_login WITH
    LOGIN
    NOSUPERUSER
    NOINHERIT
    NOCREATEDB
    NOCREATEROLE
    NOREPLICATION
    NOBYPASSRLS
    CONNECTION LIMIT 5
    ENCRYPTED PASSWORD 'SCRAM-SHA-256$4096:AKVCAySzvEjk1qtVw9JB9Q==$Mi+0cxtlwWR/q2s1uHdnoGs2eJkWZG0Ah5UPi9QrcOE=:3qa+BAcGnisr35gDUEBtt0ZavQ2jYeoEIjfZ0hIx6rg=';

DROP ROLE IF EXISTS beta_application;
CREATE ROLE beta_application WITH
    NOLOGIN
    NOSUPERUSER
    NOINHERIT
    NOCREATEDB
    NOCREATEROLE
    NOREPLICATION
    NOBYPASSRLS
    ENCRYPTED PASSWORD 'SCRAM-SHA-256$4096:AKVCAySzvEjk1qtVw9JB9Q==$Mi+0cxtlwWR/q2s1uHdnoGs2eJkWZG0Ah5UPi9QrcOE=:3qa+BAcGnisr35gDUEBtt0ZavQ2jYeoEIjfZ0hIx6rg=';

GRANT beta_application TO beta_login;



