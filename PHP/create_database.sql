--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.10
-- Dumped by pg_dump version 9.6.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE capstone;
--
-- Name: capstone; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE capstone WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


ALTER DATABASE capstone OWNER TO postgres;

\connect capstone

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: topology; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA topology;


ALTER SCHEMA topology OWNER TO admin;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry, geography, and raster spatial types and functions';


--
-- Name: postgis_topology; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS postgis_topology WITH SCHEMA topology;


--
-- Name: EXTENSION postgis_topology; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis_topology IS 'PostGIS topology spatial types and functions';


--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: riddles; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE riddles (
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    riddle text,
    answer text,
    user_id uuid,
    created timestamp without time zone DEFAULT now(),
    location geometry(Point,4326)
);


ALTER TABLE riddles OWNER TO admin;

--
-- Name: solved_riddles; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE solved_riddles (
    user_id uuid NOT NULL,
    riddle_id uuid NOT NULL,
    created timestamp without time zone DEFAULT now()
);


ALTER TABLE solved_riddles OWNER TO admin;

--
-- Name: users; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE users (
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    email text,
    password text,
    first_name text,
    last_name text,
    created timestamp without time zone DEFAULT now()
);


ALTER TABLE users OWNER TO admin;

--
-- Data for Name: riddles; Type: TABLE DATA; Schema: public; Owner: admin
--
INSERT INTO riddles VALUES ('6625bb43-2d37-43e2-8b33-b4bc70713e03', 'You used this device to develop me', 'computer', 'ca4ff8e6-0027-4769-a3f1-f385e4e0310a', '2017-12-18 15:54:43.854391', '0101000020E6100000CA350532BBCE52C0FEFD18294A914540');


--
-- Data for Name: solved_riddles; Type: TABLE DATA; Schema: public; Owner: admin
--
INSERT INTO solved_riddles VALUES ('ca4ff8e6-0027-4769-a3f1-f385e4e0310a', '6625bb43-2d37-43e2-8b33-b4bc70713e03', '2017-12-18 15:55:30.274945');


--
-- Data for Name: spatial_ref_sys; Type: TABLE DATA; Schema: public; Owner: admin
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO users VALUES ('ca4ff8e6-0027-4769-a3f1-f385e4e0310a', 'youkerm@sunyit.edu', 'test', 'Mitch', 'Youker', '2017-11-30 00:43:19.344969');


SET search_path = topology, pg_catalog;

--
-- Data for Name: topology; Type: TABLE DATA; Schema: topology; Owner: admin
--



--
-- Data for Name: layer; Type: TABLE DATA; Schema: topology; Owner: admin
--



SET search_path = public, pg_catalog;

--
-- Name: riddles riddles_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY riddles
    ADD CONSTRAINT riddles_pkey PRIMARY KEY (id);


--
-- Name: solved_riddles user_riddles_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY solved_riddles
    ADD CONSTRAINT user_riddles_pkey PRIMARY KEY (user_id, riddle_id);


--
-- Name: users user_uuid; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY users
    ADD CONSTRAINT user_uuid PRIMARY KEY (id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: index_riddles_geo; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX index_riddles_geo ON riddles USING gist (location);


--
-- Name: riddles riddles_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY riddles
    ADD CONSTRAINT riddles_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: solved_riddles user_riddles_riddle_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY solved_riddles
    ADD CONSTRAINT user_riddles_riddle_id_fkey FOREIGN KEY (riddle_id) REFERENCES riddles(id);


--
-- Name: solved_riddles user_riddles_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY solved_riddles
    ADD CONSTRAINT user_riddles_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

