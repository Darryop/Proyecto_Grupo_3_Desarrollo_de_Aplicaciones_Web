-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS KVestetica;
USE KVestetica;

-- Tabla: usuarios
CREATE TABLE usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(15),
    tipo ENUM('CLIENTE', 'ADMIN') NOT NULL,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla: roles (NUEVA - requerida por Spring Security)
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT
);

-- Tabla: usuario_roles (NUEVA - relación many-to-many)
CREATE TABLE usuario_roles (
    usuario_id INT NOT NULL,
    rol_id INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Tabla: categorias_productos
CREATE TABLE categorias_productos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla: categorias_tratamientos
CREATE TABLE categorias_tratamientos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla: productos
CREATE TABLE productos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo_producto VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    categoria_id INT,
    imagen_url VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categorias_productos(id)
);

-- Tabla: tratamientos (ACTUALIZADA)
CREATE TABLE tratamientos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo_tratamiento VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    duracion_minutos INT NOT NULL,
    categoria_id INT,
    imagen_url VARCHAR(255),
    requiere_consulta BOOLEAN DEFAULT FALSE,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (categoria_id) REFERENCES categorias_tratamientos(id)
);

-- Tabla: citas
CREATE TABLE citas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    tratamiento_id INT NOT NULL,
    fecha_cita DATETIME NOT NULL,
    estado ENUM('PENDIENTE', 'CONFIRMADA', 'COMPLETADA', 'CANCELADA') DEFAULT 'PENDIENTE',
    notas TEXT,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (tratamiento_id) REFERENCES tratamientos(id)
);

-- Tabla: carrito_compras
CREATE TABLE carrito_compras (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('ACTIVO', 'PAGADO', 'ABANDONADO') DEFAULT 'ACTIVO',
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabla: items_carrito
CREATE TABLE items_carrito (
    id INT PRIMARY KEY AUTO_INCREMENT,
    carrito_id INT NOT NULL,
    producto_id INT,
    cita_id INT,
    cantidad INT NOT NULL DEFAULT 1,
    tipo ENUM('PRODUCTO', 'CITA') NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (carrito_id) REFERENCES carrito_compras(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id),
    FOREIGN KEY (cita_id) REFERENCES citas(id),
    CHECK (
        (tipo = 'PRODUCTO' AND producto_id IS NOT NULL AND cita_id IS NULL) OR 
        (tipo = 'CITA' AND cita_id IS NOT NULL AND producto_id IS NULL)
    )
);

-- Tabla: ventas
CREATE TABLE ventas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    carrito_id INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    fecha_venta DATETIME DEFAULT CURRENT_TIMESTAMP,
    metodo_pago ENUM('TARJETA', 'EFECTIVO', 'TRANSFERENCIA') NOT NULL,
    estado ENUM('PENDIENTE', 'COMPLETADA', 'RECHAZADA') DEFAULT 'PENDIENTE',
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (carrito_id) REFERENCES carrito_compras(id)
);

-- Tabla: reseñas
CREATE TABLE reseñas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    producto_id INT,
    tratamiento_id INT,
    calificacion INT NOT NULL CHECK (calificacion >= 1 AND calificacion <= 5),
    comentario TEXT,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    tipo ENUM('PRODUCTO', 'TRATAMIENTO', 'GENERAL') NOT NULL,
    aprobada BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id),
    FOREIGN KEY (tratamiento_id) REFERENCES tratamientos(id),
    CHECK (
        (tipo = 'PRODUCTO' AND producto_id IS NOT NULL AND tratamiento_id IS NULL) OR
        (tipo = 'TRATAMIENTO' AND tratamiento_id IS NOT NULL AND producto_id IS NULL) OR
        (tipo = 'GENERAL' AND producto_id IS NULL AND tratamiento_id IS NULL)
    )
);

-- Tabla: configuracion_citas
CREATE TABLE configuracion_citas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    max_citas_dia INT DEFAULT 10,
    horario_apertura TIME DEFAULT '09:00:00',
    horario_cierre TIME DEFAULT '18:00:00',
    duracion_minima_cita INT DEFAULT 30,
    dias_anticipacion INT DEFAULT 7,
    dias_disponibles JSON
);

-- Insertar roles PRIMERO (antes de los usuarios)
INSERT INTO roles (nombre, descripcion) VALUES
('ROLE_CLIENTE', 'Usuario cliente que puede comprar productos y agendar citas'),
('ROLE_ADMIN', 'Administrador con acceso completo al sistema'),
('ROLE_EMPLEADO', 'Empleado de la estética con permisos limitados');

-- Insertar usuario administrador por defecto
INSERT INTO usuarios (email, password, nombre, apellido, telefono, tipo, fecha_registro, activo) 
VALUES ('admin@kvestetica.com', '$2y$10$9tcftrNn7bCwQZVnPk61guBg0WHMKGNR18zAnF30HxV9vzBjRfOXu', 'Administrador', 'Principal', '0000000000', 'ADMIN', NOW(), TRUE);

-- Asignar rol de admin al usuario admin
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id 
FROM usuarios u, roles r 
WHERE u.email = 'admin@kvestetica.com' AND r.nombre = 'ROLE_ADMIN';

-- Insertar configuración por defecto para citas
INSERT INTO configuracion_citas (max_citas_dia, horario_apertura, horario_cierre, duracion_minima_cita, dias_anticipacion, dias_disponibles) 
VALUES (10, '09:00:00', '18:00:00', 30, 7, '["LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO"]');

-- Insertar algunas categorías de ejemplo
INSERT INTO categorias_productos (nombre, descripcion) VALUES
('Cuidado Facial', 'Productos para el cuidado y tratamiento facial'),
('Maquillaje', 'Productos de maquillaje y cosméticos'),
('Cuidado Corporal', 'Productos para el cuidado del cuerpo');

INSERT INTO categorias_tratamientos (nombre, descripcion) VALUES
('Faciales', 'Tratamientos especializados para el rostro'),
('Corporales', 'Tratamientos para el cuerpo'),
('Depilación', 'Servicios de depilación');

-- Insertar algunos productos de ejemplo
INSERT INTO productos (codigo_producto, nombre, descripcion, precio, stock, categoria_id, imagen_url) VALUES
('PROD-001', 'Crema Hidratante', 'Crema hidratante para piel seca', 25.99, 50, 1, 'https://i.pinimg.com/1200x/1f/57/6d/1f576d517e613f24239de6c798de81b0.jpg'),
('PROD-002', 'Base de Maquillaje', 'Base de larga duración', 35.50, 30, 2, 'https://i.pinimg.com/1200x/f1/ef/6c/f1ef6c3bc5c9c5c05a591e701f986287.jpg'),
('PROD-003', 'Aceite Corporal', 'Aceite nutritivo para el cuerpo', 18.75, 40, 3, 'https://i.pinimg.com/1200x/9f/e0/6f/9fe06f9e489e2080c4eb283fad0a1293.jpg');

-- Insertar algunos tratamientos de ejemplo (ACTUALIZADO)
INSERT INTO tratamientos (codigo_tratamiento, nombre, descripcion, precio, duracion_minutos, categoria_id, imagen_url, requiere_consulta) VALUES
('TRAT-001', 'Limpieza Facial', 'Limpieza profunda del rostro', 50.00, 60, 1, 'https://i.pinimg.com/1200x/8c/9f/a7/8c9fa7dbc6e87d9a2d83c5bf0acf7874.jpg', FALSE),
('TRAT-002', 'Masaje Relajante', 'Masaje corporal relajante', 75.00, 90, 2, 'https://i.pinimg.com/736x/97/04/cf/9704cfbb81bba3bd1f9570059245e093.jpg', FALSE),
('TRAT-003', 'Depilación Láser', 'Sesión de depilación láser', 120.00, 45, 3, 'https://i.pinimg.com/736x/c8/21/09/c82109dbc84f0b4d8dd0b353fe3da949.jpg', TRUE);

-- Crear usuario webAdmin
CREATE USER IF NOT EXISTS 'webAdmin'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON KVestetica.* TO 'webAdmin'@'localhost';
FLUSH PRIVILEGES;

-- Mostrar mensaje de confirmación
SELECT 'Base de datos KVestetica creada exitosamente!' AS Mensaje;