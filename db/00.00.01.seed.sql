--
-- PostgreSQL database dump
--

-- Dumped from database version 16.4
-- Dumped by pg_dump version 16.2

-- Started on 2024-10-01 13:24:15

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_table_access_method = heap;

--
-- TOC entry 215 (class 1259 OID 18032)
-- Name: execution; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.execution (
    id uuid NOT NULL,
    type character varying(100) NOT NULL,
    command character varying(200) NOT NULL,
    result text,
    status character varying(100) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    executed_at timestamp without time zone,
    finished_at timestamp without time zone,
    user_id uuid NOT NULL
);


--
-- TOC entry 216 (class 1259 OID 18095)
-- Name: user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."user" (
    id uuid NOT NULL,
    first_name character varying(200) NOT NULL,
    last_name character varying(200) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    is_active character varying(100) NOT NULL,
    timezone character varying(200) NOT NULL,
    culture character varying(200) NOT NULL,
    language character varying(200) NOT NULL,
    subject_id character varying(150) NOT NULL
);


--
-- TOC entry 217 (class 1259 OID 18120)
-- Name: version_info; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.version_info (
    key character varying(300) NOT NULL,
    version character varying(300) NOT NULL,
    released_at timestamp without time zone NOT NULL,
    deployed_at timestamp without time zone NOT NULL,
    description character varying(300) NOT NULL
);


--
-- TOC entry 3405 (class 0 OID 18032)
-- Dependencies: 215
-- Data for Name: execution; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3406 (class 0 OID 18095)
-- Dependencies: 216
-- Data for Name: user; Type: TABLE DATA; Schema: public; Owner: -
--


--
-- TOC entry 3407 (class 0 OID 18120)
-- Dependencies: 217
-- Data for Name: version_info; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 3254 (class 2606 OID 18136)
-- Name: execution execution_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.execution
    ADD CONSTRAINT execution_pkey PRIMARY KEY (id);


--
-- TOC entry 3256 (class 2606 OID 18170)
-- Name: user user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- TOC entry 3258 (class 2606 OID 18174)
-- Name: user user_subject_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_subject_id_key UNIQUE (subject_id);


--
-- TOC entry 3260 (class 2606 OID 18176)
-- Name: version_info version_info_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.version_info
    ADD CONSTRAINT version_info_pkey PRIMARY KEY (key);


--
-- TOC entry 3261 (class 2606 OID 18202)
-- Name: execution fk_execution; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.execution
    ADD CONSTRAINT fk_execution FOREIGN KEY (user_id) REFERENCES public."user"(id);


-- Completed on 2024-10-01 13:24:22

--
-- PostgreSQL database dump complete
--

