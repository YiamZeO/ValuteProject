PGDMP         &                {            valutedb    15.4    15.4 
                0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    24686    valutedb    DATABASE     |   CREATE DATABASE valutedb WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Russian_Russia.1251';
    DROP DATABASE valutedb;
                postgres    false                        2615    24687    valute_schema    SCHEMA        CREATE SCHEMA valute_schema;
    DROP SCHEMA valute_schema;
                postgres    false            �            1259    24688    valute_curses    TABLE     w   CREATE TABLE valute_schema.valute_curses (
    id text NOT NULL,
    date date NOT NULL,
    value double precision
);
 (   DROP TABLE valute_schema.valute_curses;
       valute_schema         heap    postgres    false    6            �            1259    24693    valute_types    TABLE     x   CREATE TABLE valute_schema.valute_types (
    id text NOT NULL,
    name text,
    eng_name text,
    nominal bigint
);
 '   DROP TABLE valute_schema.valute_types;
       valute_schema         heap    postgres    false    6            j           2606    24699     valute_curses valute_curses_pkey 
   CONSTRAINT     k   ALTER TABLE ONLY valute_schema.valute_curses
    ADD CONSTRAINT valute_curses_pkey PRIMARY KEY (id, date);
 Q   ALTER TABLE ONLY valute_schema.valute_curses DROP CONSTRAINT valute_curses_pkey;
       valute_schema            postgres    false    215    215            l           2606    24701    valute_types valute_types_pkey 
   CONSTRAINT     c   ALTER TABLE ONLY valute_schema.valute_types
    ADD CONSTRAINT valute_types_pkey PRIMARY KEY (id);
 O   ALTER TABLE ONLY valute_schema.valute_types DROP CONSTRAINT valute_types_pkey;
       valute_schema            postgres    false    216            m           2606    24702 /   valute_curses valute_curses_valute_type_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY valute_schema.valute_curses
    ADD CONSTRAINT valute_curses_valute_type_id_fkey FOREIGN KEY (id) REFERENCES valute_schema.valute_types(id) ON UPDATE CASCADE ON DELETE CASCADE;
 `   ALTER TABLE ONLY valute_schema.valute_curses DROP CONSTRAINT valute_curses_valute_type_id_fkey;
       valute_schema          postgres    false    215    216    3180           