# Feature: Listado de productos (/api/products)

> Semilla de negocio. Modo: borrador crudo antes de la spec formal del planner.

## 1. Resumen
El pricing-service solo expone hoy el catálogo de marcas, por lo que un consumidor de la API no puede saber qué productos existen a la hora de lanzar consultas de precios. Se añade un maestro de productos de solo lectura, homogéneo con el enfoque ya existente para brands, que da visibilidad del catálogo de productos precargado.

## 2. Historias de usuario
- Como consumidor de la API, quiero listar los productos disponibles del catálogo, para descubrir sobre qué productos puedo consultar precios.
- Como consumidor de la API, quiero filtrar el listado de productos con un texto libre opcional (`q`), para localizar rápidamente un producto sin recorrer la lista completa.

## 3. Objetivos y criterios de éxito
- Objetivo de negocio: exponer el maestro de productos como catálogo de consulta, replicando la experiencia yต homogeneidad del listing de brands.
- Criterio de éxito medible: un listado `GET /api/products` respondiendo 200 con el catálogo precargado, con filtro `q` que Filtra por coincidencia parcial insensible a mayúsculas; cubierto por tests unit, repository y e2e (mismo nivel de cobertura que brands).

## 4. Actores
- Consumidor de la API: único rol de usuario; consulta el catálogo de productos (solo lectura).
- Product (concepto DDD): nuevo concepto del catálogo, maestro independiente.
- Brand (concepto DDD existente): patrón de referencia a imitar; no sufre cambios.
- Price (concepto DDD existente): solo se menciona como consumidor a futuro del id del producto; no cambia en esta feature.

## 5. Reglas de negocio de alto nivel
- R1: el listado devuelve el catálogo completo de productos precargado.
- R2: filtro opcional `q` (texto libre): coincidencia parcial (contains) e insensible a mayúsculas/minúsculas sobre los campos textuales del producto.
- R3: cuando no existen productos o ninguno coincide con `q`, la respuesta es 200 con lista vacía (el listado vacío es un resultado válido, no un error).
- R4: solo lectura — no hay endpoints de escritura; los productos provienen únicamente del catálogo precargado vía seed.
- R5: no se garantiza un orden concreto de la colección devuelta (se devuelve tal cual la proporciona la persistencia).

## 6. Casos y casos borde destacados
- Consulta sin filtro con catálogo precargado → 200, todos los productos.
- Consulta con `q` que coincide (parcial y case-insensitive) → 200, solo productos coincidentes.
- Consulta con `q` sin coincidencias → 200, lista vacía.
- Catálogo sin productos (seed vacío/limpio) → 200, lista vacía.

## 7. Fuera de alcance
- CRUD de productos (create/update/delete) y cambio de estado de productos.
- Endpoint de detalle de producto por id.
- Paginación u ordenación de resultados.
- Vínculos product↔price o product↔brand expuestos en la respuesta (los precios ya referencian productos por id; no se cambia nada ahí).
- Cualquier modificación en `GET /api/prices/active` o en el flujo de selección de precios.

## 8. Supuestos y dudas abiertas
- Supuesto: el patrón técnico a seguir es exactamente el del listado de brands (API-first → codegen, hexagonal, tags de test unit/repository/e2e) — decisión del planner.
- Supuesto: el seed de productos se añade a la migración Liquibase existente (decisión del planner, sujeto a la constitución: Liquibase como única autoridad del esquema).
- Duda abierta a resolver en la spec técnica: campos exactos del producto y del DTO de respuesta (mínimo elicitado: id + nombre + descripción).
