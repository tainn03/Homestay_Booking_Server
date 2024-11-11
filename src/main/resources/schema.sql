create table if not exists public.amenity
(
    name varchar(255) not null
    primary key,
    type varchar(255)
    );

alter table public.amenity
    owner to postgres;

create table if not exists public.city
(
    id   integer not null
    primary key,
    name varchar(255)
    );

alter table public.city
    owner to postgres;

create table if not exists public.district
(
    city_id integer
    constraint fksgx09prp6sk2f0we38bf2dtal
    references public.city,
    id      integer not null
    primary key,
    detail  varchar(255),
    name    varchar(255)
    );

alter table public.district
    owner to postgres;

create table if not exists public.role
(
    created_at timestamp(6),
    updated_at timestamp(6),
    created_by varchar(255),
    role_name  varchar(255) not null
    primary key,
    updated_by varchar(255)
    );

alter table public.role
    owner to postgres;

create table if not exists public.permission
(
    permission varchar(255) not null
    primary key,
    role_name  varchar(255) not null
    constraint fk5up9ph16q6g3lbhmd44jjuntn
    references public.role
    );

alter table public.permission
    owner to postgres;

create table if not exists public.type_homestay
(
    name      varchar(255) not null
    primary key,
    url_image varchar(255)
    );

alter table public.type_homestay
    owner to postgres;

create table if not exists public.users
(
    dob              date,
    created_at       timestamp(6),
    last_login       timestamp(6),
    updated_at       timestamp(6),
    address          varchar(255),
    bank_name        varchar(255),
    bank_num         varchar(255),
    bank_username    varchar(255),
    business_license varchar(255),
    cccd             varchar(255),
    created_by       varchar(255),
    email            varchar(255),
    full_name        varchar(255),
    gender           varchar(255),
    id               varchar(255) not null
    primary key,
    nationality      varchar(255),
    password         varchar(255),
    phone            varchar(255),
    role             varchar(255) not null
    constraint fk20wcxq3dnh6io9qug4vc90p0p
    references public.role,
    status           varchar(255),
    updated_by       varchar(255)
    );

alter table public.users
    owner to postgres;

create table if not exists public.booking
(
    check_in       date,
    check_out      date,
    guests         integer      not null,
    original_total integer      not null,
    total_cost     integer      not null,
    total_discount integer      not null,
    created_at     timestamp(6),
    updated_at     timestamp(6),
    version        bigint,
    created_by     varchar(255),
    id             varchar(255) not null
    primary key,
    note           varchar(255),
    status         varchar(255),
    updated_by     varchar(255),
    user_id        varchar(255) not null
    constraint fk7udbel7q86k041591kj6lfmvw
    references public.users
    );

alter table public.booking
    owner to postgres;

create table if not exists public.conversation
(
    id       varchar(255) not null
    primary key,
    user1_id varchar(255)
    constraint fk8w8m9o8kqh3roso7nuqt559f8
    references public.users,
    user2_id varchar(255)
    constraint fk7abdberik1kltbsfirjgd5o1k
    references public.users
    );

alter table public.conversation
    owner to postgres;

create table if not exists public.homestay
(
    district_id        integer      not null
    constraint fknjlswfam1elwd2qqes2wrevb8
    references public.district,
    latitude           double precision,
    longitude          double precision,
    created_at         timestamp(6),
    updated_at         timestamp(6),
    version            bigint,
    address_detail     varchar(255),
    created_by         varchar(255),
    email              varchar(255),
    id                 varchar(255) not null
    primary key,
    name               varchar(255) not null
    constraint fkr7urdsb60cyjwq7rsmyyyv0so
    references public.type_homestay,
    name_homestay      varchar(255),
    phone              varchar(255),
    standard_check_in  varchar(255),
    standard_check_out varchar(255),
    status             varchar(255),
    updated_by         varchar(255),
    user_id            varchar(255) not null
    constraint fkimrx31m1fswnwgwjbl0s94spo
    references public.users
    );

alter table public.homestay
    owner to postgres;

create table if not exists public.favorite
(
    homestay_id varchar(255) not null
    constraint fkivpi5lo2s1bt0wxix981vpte2
    references public.homestay,
    user_id     varchar(255) not null
    constraint fka2lwa7bjrnbti5v12mga2et1y
    references public.users,
    primary key (homestay_id, user_id)
    );

alter table public.favorite
    owner to postgres;

create table if not exists public.message
(
    read            boolean      not null,
    created_at      timestamp(6),
    updated_at      timestamp(6),
    conversation_id varchar(255)
    constraint fk6yskk3hxw5sklwgi25y6d5u1l
    references public.conversation,
    created_by      varchar(255),
    id              varchar(255) not null
    primary key,
    receiver        varchar(255),
    sender          varchar(255),
    text            varchar(255),
    updated_by      varchar(255)
    );

alter table public.message
    owner to postgres;

create table if not exists public.payment
(
    amount         integer      not null,
    date           date,
    created_at     timestamp(6),
    updated_at     timestamp(6),
    booking_id     varchar(255)
    unique
    constraint fkqewrl4xrv9eiad6eab3aoja65
    references public.booking,
    created_by     varchar(255),
    id             varchar(255) not null
    primary key,
    note           varchar(255),
    payment_method varchar(255),
    status         varchar(255),
    transaction_id varchar(255),
    updated_by     varchar(255)
    );

alter table public.payment
    owner to postgres;

create table if not exists public.review
(
    rating      integer,
    created_at  timestamp(6),
    updated_at  timestamp(6),
    comment     varchar(255),
    created_by  varchar(255),
    homestay_id varchar(255) not null
    constraint fkb6qm6qgbdhf2tdcg1k4clmhft
    references public.homestay,
    id          varchar(255) not null
    primary key,
    updated_by  varchar(255),
    user_id     varchar(255) not null
    constraint fk6cpw2nlklblpvc7hyt7ko6v3e
    references public.users
    );

alter table public.review
    owner to postgres;

create table if not exists public.room
(
    price         double precision not null,
    size          integer          not null,
    weekend_price double precision not null,
    created_at    timestamp(6),
    updated_at    timestamp(6),
    version       bigint,
    created_by    varchar(255),
    homestay_id   varchar(255)     not null
    constraint fkqcvstr3bg2lmknhdjwitgr3jm
    references public.homestay,
    id            varchar(255)     not null
    primary key,
    name          varchar(255),
    status        varchar(255),
    updated_by    varchar(255)
    );

alter table public.room
    owner to postgres;

create table if not exists public.booking_room
(
    booking_id varchar(255) not null
    constraint fk9umnt0pjb1nf83qwoqry1cuc2
    references public.booking,
    room_id    varchar(255) not null
    constraint fk4e002f18klgu08ekxnav2rwr9
    references public.room
    );

alter table public.booking_room
    owner to postgres;

create table if not exists public.discount
(
    value       double precision not null,
    created_at  timestamp(6),
    end_date    timestamp(6),
    start_date  timestamp(6),
    updated_at  timestamp(6),
    version     bigint,
    created_by  varchar(255),
    description varchar(255),
    homestay_id varchar(255)
    constraint fkju5lcoa8yde6c8lfjiud11gpq
    references public.homestay,
    id          varchar(255)     not null
    primary key,
    room_id     varchar(255)
    constraint fk7dg30c6ng5s3cpwmv0tdu09bm
    references public.room,
    type        varchar(255),
    updated_by  varchar(255)
    );

alter table public.discount
    owner to postgres;

create table if not exists public.image
(
    created_at  timestamp(6),
    updated_at  timestamp(6),
    version     bigint,
    created_by  varchar(255),
    homestay_id varchar(255)
    constraint fkqjvqum3p4hbxuy6ue1wb8jmc7
    references public.homestay,
    id          varchar(255) not null
    primary key,
    room_id     varchar(255)
    constraint fkgfoef2g9bwlndgk8ttmf902dg
    references public.room,
    updated_by  varchar(255),
    url         varchar(255),
    user_id     varchar(255)
    unique
    constraint fkcvpnctgluno47ac6avana5sqf
    references public.users
    );

alter table public.image
    owner to postgres;

create table if not exists public.price_calendar
(
    price   double precision not null,
    date    varchar(255),
    id      varchar(255)     not null
    primary key,
    room_id varchar(255)
    constraint fknaxqalgga6rmif2v0m26rh5f5
    references public.room
    );

alter table public.price_calendar
    owner to postgres;

create table if not exists public.room_amenity
(
    amenity_id varchar(255) not null
    constraint fkaj52mcu9d5c3dglfx9f07qi94
    references public.amenity,
    room_id    varchar(255) not null
    constraint fkehqkxcxw77umgbfgk0sbwbly1
    references public.room,
    primary key (amenity_id, room_id)
    );

alter table public.room_amenity
    owner to postgres;

create table if not exists public.token
(
    expired    boolean      not null,
    revoked    boolean      not null,
    created_at timestamp(6),
    updated_at timestamp(6),
    token      varchar(1024),
    created_by varchar(255),
    id         varchar(255) not null
    primary key,
    token_type varchar(255)
    constraint token_token_type_check
    check ((token_type)::text = 'BEARER'::text),
    updated_by varchar(255),
    user_id    varchar(255) not null
    constraint fkj8rfw4x0wjjyibfqq566j4qng
    references public.users
    );

alter table public.token
    owner to postgres;

create table if not exists public.refund
(
    id         varchar(255) not null
    primary key,
    date       timestamp(6),
    reason     varchar(255),
    payment_id varchar(255)
    constraint ukqwu73qgmbrsnysqx67oewyj5d
    unique
    constraint fkeoh1147brjy6m009cswl5lty4
    references public.payment
    );

alter table public.refund
    owner to postgres;

