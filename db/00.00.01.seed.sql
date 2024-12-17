--
-- PostgreSQL database dump
--

-- Dumped from database version 16.4
-- Dumped by pg_dump version 17.0

-- Started on 2024-12-17 12:26:40

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 219 (class 1259 OID 16440)
-- Name: UserContactInfo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."UserContactInfo" (
    id uuid NOT NULL,
    "user" uuid NOT NULL,
    ordinal integer DEFAULT 0 NOT NULL,
    type smallint NOT NULL,
    value character varying(512) NOT NULL,
    created_at timestamp without time zone NOT NULL
);


--
-- TOC entry 218 (class 1259 OID 16428)
-- Name: UserRole; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."UserRole" (
    id uuid NOT NULL,
    "user" uuid NOT NULL,
    role character varying(512) NOT NULL,
    created_at timestamp without time zone NOT NULL
);


--
-- TOC entry 215 (class 1259 OID 16385)
-- Name: execution; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.execution (
    id uuid NOT NULL,
    command character varying(200) NOT NULL,
    result text,
    status character varying(100) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    executed_at timestamp without time zone,
    finished_at timestamp without time zone,
    user_id uuid NOT NULL,
    type smallint NOT NULL
);


--
-- TOC entry 216 (class 1259 OID 16390)
-- Name: user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."user" (
    id uuid NOT NULL,
    first_name character varying(200) NOT NULL,
    last_name character varying(200) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    timezone character varying(200) NOT NULL,
    culture character varying(200) NOT NULL,
    language character varying(200) NOT NULL,
    subject_id character varying(150) NOT NULL,
    is_active smallint NOT NULL
);


--
-- TOC entry 217 (class 1259 OID 16395)
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
-- TOC entry 3273 (class 2606 OID 16447)
-- Name: UserContactInfo UserContactInfo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."UserContactInfo"
    ADD CONSTRAINT "UserContactInfo_pkey" PRIMARY KEY (id);


--
-- TOC entry 3271 (class 2606 OID 16434)
-- Name: UserRole UserRole_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."UserRole"
    ADD CONSTRAINT "UserRole_pkey" PRIMARY KEY (id);


--
-- TOC entry 3263 (class 2606 OID 16401)
-- Name: execution execution_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.execution
    ADD CONSTRAINT execution_pkey PRIMARY KEY (id);


--
-- TOC entry 3265 (class 2606 OID 16403)
-- Name: user user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- TOC entry 3267 (class 2606 OID 16405)
-- Name: user user_subject_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_subject_id_key UNIQUE (subject_id);


--
-- TOC entry 3269 (class 2606 OID 16407)
-- Name: version_info version_info_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.version_info
    ADD CONSTRAINT version_info_pkey PRIMARY KEY (key);


--
-- TOC entry 3276 (class 2606 OID 16448)
-- Name: UserContactInfo UserContactInfo_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."UserContactInfo"
    ADD CONSTRAINT "UserContactInfo_user_fkey" FOREIGN KEY ("user") REFERENCES public."user"(id);


--
-- TOC entry 3275 (class 2606 OID 16435)
-- Name: UserRole UserRole_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."UserRole"
    ADD CONSTRAINT "UserRole_user_fkey" FOREIGN KEY ("user") REFERENCES public."user"(id);


--
-- TOC entry 3274 (class 2606 OID 16408)
-- Name: execution fk_execution; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.execution
    ADD CONSTRAINT fk_execution FOREIGN KEY (user_id) REFERENCES public."user"(id);


-- Completed on 2024-12-17 12:26:41

--
-- PostgreSQL database dump complete
--

