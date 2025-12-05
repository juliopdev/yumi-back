-- Tabla principal de secciones
CREATE TABLE about_sections (
  id SERIAL PRIMARY KEY,
  key VARCHAR(50) UNIQUE NOT NULL, -- 'about', 'faq', 'policies'
  title VARCHAR(255) NOT NULL,
  subtitle TEXT
);

-- Tarjetas con iconos
CREATE TABLE about_cards (
  id SERIAL PRIMARY KEY,
  section_key VARCHAR(50) REFERENCES about_sections(key) ON DELETE CASCADE,
  icon VARCHAR(50) NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT NOT NULL
);

-- Preguntas frecuentes
CREATE TABLE about_faqs (
  id SERIAL PRIMARY KEY,
  question TEXT NOT NULL,
  answer TEXT NOT NULL
);

-- Políticas (privacidad, términos, etc.)
CREATE TABLE about_policies (
  id SERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL
);