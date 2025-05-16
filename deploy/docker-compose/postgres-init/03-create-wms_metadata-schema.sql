--
-- PostgreSQL database dump
--

-- Dumped from database version 17.3
-- Dumped by pg_dump version 17.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
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
-- Name: bounding_boxes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bounding_boxes (
    id integer NOT NULL,
    min_x double precision NOT NULL,
    min_y double precision NOT NULL,
    max_x double precision NOT NULL,
    max_y double precision NOT NULL
);


ALTER TABLE public.bounding_boxes OWNER TO postgres;

--
-- Name: bounding_boxes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bounding_boxes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bounding_boxes_id_seq OWNER TO postgres;

--
-- Name: bounding_boxes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bounding_boxes_id_seq OWNED BY public.bounding_boxes.id;


--
-- Name: capability_exceptions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.capability_exceptions (
    id integer NOT NULL,
    service_id integer,
    exception_description text
);


ALTER TABLE public.capability_exceptions OWNER TO postgres;

--
-- Name: capability_exceptions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.capability_exceptions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.capability_exceptions_id_seq OWNER TO postgres;

--
-- Name: capability_exceptions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.capability_exceptions_id_seq OWNED BY public.capability_exceptions.id;


--
-- Name: capability_formats; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.capability_formats (
    id integer NOT NULL,
    service_id integer,
    format_name character varying(255) NOT NULL
);


ALTER TABLE public.capability_formats OWNER TO postgres;

--
-- Name: capability_formats_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.capability_formats_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.capability_formats_id_seq OWNER TO postgres;

--
-- Name: capability_formats_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.capability_formats_id_seq OWNED BY public.capability_formats.id;


--
-- Name: capability_operations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.capability_operations (
    id integer NOT NULL,
    service_id integer,
    operation_name character varying(255) NOT NULL
);


ALTER TABLE public.capability_operations OWNER TO postgres;

--
-- Name: capability_operations_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.capability_operations_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.capability_operations_id_seq OWNER TO postgres;

--
-- Name: capability_operations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.capability_operations_id_seq OWNED BY public.capability_operations.id;


--
-- Name: capability_urls; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.capability_urls (
    id integer NOT NULL,
    service_id integer,
    url character varying(255) NOT NULL
);


ALTER TABLE public.capability_urls OWNER TO postgres;

--
-- Name: capability_urls_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.capability_urls_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.capability_urls_id_seq OWNER TO postgres;

--
-- Name: capability_urls_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.capability_urls_id_seq OWNED BY public.capability_urls.id;


--
-- Name: layer_crs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.layer_crs (
    id integer NOT NULL,
    layer_id integer,
    crs_name character varying(255) NOT NULL
);


ALTER TABLE public.layer_crs OWNER TO postgres;

--
-- Name: layer_crs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.layer_crs_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.layer_crs_id_seq OWNER TO postgres;

--
-- Name: layer_crs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.layer_crs_id_seq OWNED BY public.layer_crs.id;


--
-- Name: layer_keywords; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.layer_keywords (
    id integer NOT NULL,
    layer_id integer,
    keyword character varying(255) NOT NULL
);


ALTER TABLE public.layer_keywords OWNER TO postgres;

--
-- Name: layer_keywords_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.layer_keywords_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.layer_keywords_id_seq OWNER TO postgres;

--
-- Name: layer_keywords_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.layer_keywords_id_seq OWNED BY public.layer_keywords.id;


--
-- Name: layers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.layers (
    id integer NOT NULL,
    service_id integer,
    name character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    abstract_description text,
    min_scale_denominator double precision,
    max_scale_denominator double precision,
    geographic_bounding_box_id integer,
    bounding_box_id integer
);


ALTER TABLE public.layers OWNER TO postgres;

--
-- Name: layers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.layers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.layers_id_seq OWNER TO postgres;

--
-- Name: layers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.layers_id_seq OWNED BY public.layers.id;


--
-- Name: service_metadata; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.service_metadata (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    max_width integer,
    max_height integer
);


ALTER TABLE public.service_metadata OWNER TO postgres;

--
-- Name: service_metadata_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.service_metadata_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.service_metadata_id_seq OWNER TO postgres;

--
-- Name: service_metadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.service_metadata_id_seq OWNED BY public.service_metadata.id;


--
-- Name: styles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.styles (
    id integer NOT NULL,
    layer_id integer,
    name character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    abstract_description text,
    legend_url character varying(255)
);


ALTER TABLE public.styles OWNER TO postgres;

--
-- Name: styles_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.styles_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.styles_id_seq OWNER TO postgres;

--
-- Name: styles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.styles_id_seq OWNED BY public.styles.id;


--
-- Name: bounding_boxes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bounding_boxes ALTER COLUMN id SET DEFAULT nextval('public.bounding_boxes_id_seq'::regclass);


--
-- Name: capability_exceptions id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.capability_exceptions ALTER COLUMN id SET DEFAULT nextval('public.capability_exceptions_id_seq'::regclass);


--
-- Name: capability_formats id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.capability_formats ALTER COLUMN id SET DEFAULT nextval('public.capability_formats_id_seq'::regclass);


--
-- Name: capability_operations id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.capability_operations ALTER COLUMN id SET DEFAULT nextval('public.capability_operations_id_seq'::regclass);


--
-- Name: capability_urls id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.capability_urls ALTER COLUMN id SET DEFAULT nextval('public.capability_urls_id_seq'::regclass);


--
-- Name: layer_crs id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.layer_crs ALTER COLUMN id SET DEFAULT nextval('public.layer_crs_id_seq'::regclass);


--
-- Name: layer_keywords id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.layer_keywords ALTER COLUMN id SET DEFAULT nextval('public.layer_keywords_id_seq'::regclass);


--
-- Name: layers id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.layers ALTER COLUMN id SET DEFAULT nextval('public.layers_id_seq'::regclass);


--
-- Name: service_metadata id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.service_metadata ALTER COLUMN id SET DEFAULT nextval('public.service_metadata_id_seq'::regclass);


--
-- Name: styles id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.styles ALTER COLUMN id SET DEFAULT nextval('public.styles_id_seq'::regclass);


--
-- Data for Name: bounding_boxes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.bounding_boxes (id, min_x, min_y, max_x, max_y) FROM stdin;
\.


--
-- Data for Name: capability_exceptions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.capability_exceptions (id, service_id, exception_description) FROM stdin;
\.


--
-- Data for Name: capability_formats; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.capability_formats (id, service_id, format_name) FROM stdin;
\.


--
-- Data for Name: capability_operations; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.capability_operations (id, service_id, operation_name) FROM stdin;
\.


--
-- Data for Name: capability_urls; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.capability_urls (id, service_id, url) FROM stdin;
\.


--
-- Data for Name: layer_crs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.layer_crs (id, layer_id, crs_name) FROM stdin;
\.


--
-- Data for Name: layer_keywords; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.layer_keywords (id, layer_id, keyword) FROM stdin;
\.


--
-- Data for Name: layers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.layers (id, service_id, name, title, abstract_description, min_scale_denominator, max_scale_denominator, geographic_bounding_box_id, bounding_box_id) FROM stdin;
\.


--
-- Data for Name: service_metadata; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.service_metadata (id, name, title, max_width, max_height) FROM stdin;
\.


--
-- Data for Name: styles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.styles (id, layer_id, name, title, abstract_description, legend_url) FROM stdin;
\.


--
-- Name: bounding_boxes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bounding_boxes_id_seq', 1, false);


--
-- Name: capability_exceptions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.capability_exceptions_id_seq', 1, false);


--
-- Name: capability_formats_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.capability_formats_id_seq', 1, false);


--
-- Name: capability_operations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.capability_operations_id_seq', 1, false);


--
-- Name: capability_urls_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.capability_urls_id_seq', 1, false);


--
-- Name: layer_crs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.layer_crs_id_seq', 1, false);


--
-- Name: layer_keywords_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.layer_keywords_id_seq', 1, false);


--
-- Name: layers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.layers_id_seq', 1, false);


--
-- Name: service_metadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.service_metadata_id_seq', 2, true);


--
-- Name: styles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.styles_id_seq', 1, false);


--
-- Name: bounding_boxes bounding_boxes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bounding_boxes
    ADD CONSTRAINT bounding_boxes_pkey PRIMARY KEY (id);


--
-- Name: capability_exceptions capability_exceptions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.capability_exceptions
    ADD CONSTRAINT capability_exceptions_pkey PRIMARY KEY (id);


--
-- Name: capability_formats capability_formats_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.capability_formats
    ADD CONSTRAINT capability_formats_pkey PRIMARY KEY (id);


--
-- Name: capability_operations capability_operations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.capability_operations
    ADD CONSTRAINT capability_operations_pkey PRIMARY KEY (id);


--
-- Name: capability_urls capability_urls_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.capability_urls
    ADD CONSTRAINT capability_urls_pkey PRIMARY KEY (id);


--
-- Name: layer_crs layer_crs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.layer_crs
    ADD CONSTRAINT layer_crs_pkey PRIMARY KEY (id);


--
-- Name: layer_keywords layer_keywords_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.layer_keywords
    ADD CONSTRAINT layer_keywords_pkey PRIMARY KEY (id);


--
-- Name: layers layers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.layers
    ADD CONSTRAINT layers_pkey PRIMARY KEY (id);


--
-- Name: service_metadata service_metadata_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.service_metadata
    ADD CONSTRAINT service_metadata_pkey PRIMARY KEY (id);


--
-- Name: styles styles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.styles
    ADD CONSTRAINT styles_pkey PRIMARY KEY (id);


--
-- Name: capability_exceptions capability_exceptions_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.capability_exceptions
    ADD CONSTRAINT capability_exceptions_service_id_fkey FOREIGN KEY (service_id) REFERENCES public.service_metadata(id) ON DELETE CASCADE;


--
-- Name: capability_formats capability_formats_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.capability_formats
    ADD CONSTRAINT capability_formats_service_id_fkey FOREIGN KEY (service_id) REFERENCES public.service_metadata(id) ON DELETE CASCADE;


--
-- Name: capability_operations capability_operations_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.capability_operations
    ADD CONSTRAINT capability_operations_service_id_fkey FOREIGN KEY (service_id) REFERENCES public.service_metadata(id) ON DELETE CASCADE;


--
-- Name: capability_urls capability_urls_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.capability_urls
    ADD CONSTRAINT capability_urls_service_id_fkey FOREIGN KEY (service_id) REFERENCES public.service_metadata(id) ON DELETE CASCADE;


--
-- Name: layer_crs layer_crs_layer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.layer_crs
    ADD CONSTRAINT layer_crs_layer_id_fkey FOREIGN KEY (layer_id) REFERENCES public.layers(id) ON DELETE CASCADE;


--
-- Name: layer_keywords layer_keywords_layer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.layer_keywords
    ADD CONSTRAINT layer_keywords_layer_id_fkey FOREIGN KEY (layer_id) REFERENCES public.layers(id) ON DELETE CASCADE;


--
-- Name: layers layers_bounding_box_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.layers
    ADD CONSTRAINT layers_bounding_box_id_fkey FOREIGN KEY (bounding_box_id) REFERENCES public.bounding_boxes(id);


--
-- Name: layers layers_geographic_bounding_box_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.layers
    ADD CONSTRAINT layers_geographic_bounding_box_id_fkey FOREIGN KEY (geographic_bounding_box_id) REFERENCES public.bounding_boxes(id);


--
-- Name: layers layers_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.layers
    ADD CONSTRAINT layers_service_id_fkey FOREIGN KEY (service_id) REFERENCES public.service_metadata(id) ON DELETE CASCADE;


--
-- Name: styles styles_layer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.styles
    ADD CONSTRAINT styles_layer_id_fkey FOREIGN KEY (layer_id) REFERENCES public.layers(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

