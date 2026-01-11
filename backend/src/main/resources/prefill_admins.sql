INSERT INTO users
(role, email, password, name, surname, address, phone, status, is_activated, is_blocked, created_at)
VALUES
    (
        'ADMIN',
        'admin1@ubre.com',
        '$2a$12$C0h68uv2yyCw3XwsyH.o7u9vB9ZT7z66koz3WU8tAT7M3zYuKnDy2', -- lozinka: admin1
        'Admin',
        'One',
        'Adresa 1',
        '0601111111',
        'INACTIVE',
        true,
        false,
        now()
    ),
    (
        'ADMIN',
        'admin2@ubre.com',
        '$2a$12$/dsTJuosyVKLe6Lf0euxSOimpkL8F1QhoyatCbyO53hiI4KOQCtFu', -- lozinka: admin2
        'Admin',
        'Two',
        'Adresa 2',
        '0602222222',
        'INACTIVE',
        true,
        false,
        now()
    )
    ON CONFLICT (email) DO NOTHING;
