# Teoría - Programación en Java

---

## 1. Orientación a Objetos

### Pilares de la OO

#### Abstracción
Consiste en modelar del mundo real solo lo que es relevante para el sistema. No representamos un libro con todos sus detalles físicos, sino solo lo que el sistema necesita: ISBN, título, autor, año.

```java
public record Libro(String isbn, String titulo, String autor, int anio) {}
// Ignoramos el color de la tapa, el peso, etc.
```

#### Encapsulamiento
Ocultar el estado interno de un objeto y exponer solo lo necesario. Los campos son `private` y se accede a ellos mediante métodos públicos.

```java
public abstract class Socio {
    private boolean bloqueado;            // nadie lo toca directamente
    private List<String> librosPrestados; // idem

    public boolean isBloqueado() { return bloqueado; }          // lectura controlada
    public void setBloqueado(boolean b) { this.bloqueado = b; } // escritura controlada
}
```

Esto evita que el estado del objeto quede inconsistente por modificaciones externas.

#### Herencia
Una clase hija hereda el estado y comportamiento de la clase padre, y puede agregar o modificar lo que necesite.

```java
public abstract class Socio {
    private final String nombre;
    public abstract int getLimitePrestamos(); // cada hijo lo define
}

public class SocioEstudiante extends Socio {
    @Override
    public int getLimitePrestamos() { return 3; }
}
```

La palabra `super` permite acceder al constructor o métodos del padre:
```java
public SocioEstudiante(int id, String nombre, String dni, String email) {
    super(id, nombre, dni, email, CategoriaSocio.ESTUDIANTE);
}
```

#### Polimorfismo
La capacidad de tratar objetos de distintos tipos de forma uniforme. Java decide en tiempo de ejecución qué método ejecutar según el tipo real del objeto.

```java
public void mostrarLimite(Socio socio) {
    System.out.println(socio.getLimitePrestamos()); // 3 o 5 según el tipo real
}

mostrarLimite(new SocioEstudiante(...)); // imprime 3
mostrarLimite(new SocioDocente(...));    // imprime 5
```

Esto se llama **dynamic dispatch**: el método que se ejecuta se resuelve en tiempo de ejecución, no en compilación.

---

### Plantillas y "perforaciones"
Una clase abstracta actúa como una plantilla con "huecos" que las subclases deben completar. Los métodos abstractos son las perforaciones: el padre define que debe existir, pero no cómo.

```java
public abstract class Socio {
    // Plantilla: define el flujo general
    public boolean puedeTomarPrestado() {
        return !bloqueado && librosPrestados.size() < getLimitePrestamos();
    }

    // Perforación: el hijo completa el detalle
    public abstract int getLimitePrestamos();
}
```

---

### Variables de referencia y Memoria
En Java, los objetos viven en el **heap** (memoria dinámica). Las variables no contienen el objeto, contienen una **referencia** (dirección de memoria) al objeto.

```java
Socio s1 = new SocioEstudiante(1, "Ana", "12345", "ana@mail.com");
Socio s2 = s1; // s2 apunta al MISMO objeto, no es una copia

s2.setBloqueado(true);
System.out.println(s1.isBloqueado()); // true, porque s1 y s2 son el mismo objeto
```

Los tipos primitivos (`int`, `boolean`, `double`) se guardan directamente en la variable, no como referencia.

---

### Responsabilidad y Colaboración
Cada clase tiene una responsabilidad clara. Cuando necesita algo que no es su responsabilidad, colabora con otra clase.

En BiblioTech:
- `PrestamoService` necesita verificar si un libro existe → colabora con `LibroRepositoryImpl`
- `PrestamoService` necesita verificar el límite del socio → colabora con `Socio`
- `Socio` sabe si puede tomar prestado → es su responsabilidad

```java
Libro libro = libroRepo.buscarPorId(isbn).orElseThrow(...);
Socio socio = socioRepo.buscarPorId(id).orElseThrow(...);
if (!socio.puedeTomarPrestado()) { ... } // Socio responde por sí mismo
```

---

### Servicios
Los servicios contienen la lógica de negocio. No saben cómo se guardan los datos (eso es del repositorio) ni cómo se muestran (eso es del Main). Solo aplican las reglas.

Un servicio típico: valida, busca, aplica reglas, guarda, retorna resultado o lanza excepción.

---

### Funcionalidades recientes

**Records (Java 16+):** clases inmutables para datos puros, generan constructor, getters, equals, hashCode y toString automáticamente.
```java
public record Libro(String isbn, String titulo) {}
```

**Switch expressions (Java 14+):** switch como expresión con flechas, sin break.
```java
switch (opcion) {
    case 1 -> menuLibros();
    case 2 -> menuSocios();
    default -> System.out.println("Inválido");
}
```

**var (Java 10+):** inferencia de tipos local.
```java
var lista = new ArrayList<Libro>(); // el compilador infiere ArrayList<Libro>
```

---

## 2. Interfaces y Colecciones

### Clases Abstractas
Una clase abstracta no se puede instanciar directamente. Puede tener métodos con implementación y métodos abstractos (sin implementación).

```java
public abstract class Socio {
    public boolean puedeTomarPrestado() { ... } // tiene implementación
    public abstract int getLimitePrestamos();    // sin implementación
}

// Socio s = new Socio(); // ERROR: no se puede instanciar
Socio s = new SocioEstudiante(...); // OK: se instancia la subclase
```

#### Clase abstracta pura
Cuando todos sus métodos son abstractos. Es casi equivalente a una interfaz, pero puede tener campos y constructor.

```java
public abstract class FiguraGeometrica {
    public abstract double calcularArea();
    public abstract double calcularPerimetro();
}
```

---

### Interfaces
Define un contrato: qué métodos debe tener una clase, sin implementación. Una clase puede implementar múltiples interfaces.

```java
public interface Repository<T, ID> {
    void guardar(T entidad);
    Optional<T> buscarPorId(ID id);
    List<T> buscarTodos();
}
```

| | Interfaz | Clase Abstracta |
|---|---|---|
| Instanciable | No | No |
| Herencia múltiple | Sí | No |
| Campos | Solo constantes | Sí |
| Constructor | No | Sí |
| Métodos con impl. | Solo `default` | Sí |

**Uso en OO:** las interfaces permiten que clases muy distintas compartan un contrato. `LibroRepositoryImpl` y `SocioRepositoryImpl` son muy distintas pero ambas implementan `Repository`.

---

### Colecciones

#### Lista (`List`)
Ordenada, permite duplicados. Cada elemento tiene un índice.

```java
List<String> libros = new ArrayList<>();
libros.add("Clean Code");
libros.add("Clean Code"); // permite duplicados
libros.get(0);            // acceso por índice
```

#### Set
No permite duplicados, no garantiza orden.

```java
Set<String> categorias = new HashSet<>();
categorias.add("Ficción");
categorias.add("Ficción"); // ignorado, ya existe
```

#### Mapa (`Map`)
Pares clave-valor. La clave es única. Acceso directo por clave en O(1).

```java
Map<String, Libro> storage = new HashMap<>();
storage.put("978-123", libro);       // guardar
storage.get("978-123");              // buscar por clave
storage.containsKey("978-123");      // verificar existencia
```

En BiblioTech usamos `HashMap` en los repositorios porque el acceso por ISBN o ID es instantáneo.

---

### Iterar colecciones

```java
List<Libro> libros = libroRepo.buscarTodos();

// For-each clásico
for (Libro libro : libros) {
    System.out.println(libro.titulo());
}

// forEach con lambda
libros.forEach(libro -> System.out.println(libro.titulo()));

// forEach con method reference
libros.forEach(System.out::println);

// Iterar un Map
storage.forEach((isbn, libro) -> System.out.println(isbn + ": " + libro.titulo()));
```

---

### Colecciones de fábrica (Java 9+)
Crean colecciones inmutables de forma concisa.

```java
List<String> lista = List.of("uno", "dos", "tres"); // inmutable
Set<String> set = Set.of("a", "b", "c");            // inmutable
Map<String, Integer> mapa = Map.of("clave", 1);     // inmutable
```

No se pueden modificar: `lista.add("cuatro")` lanza `UnsupportedOperationException`.

---

### Inferencia de tipos - var
El compilador infiere el tipo según lo que se asigna. Solo funciona en variables locales.

```java
var lista = new ArrayList<Libro>();      // infiere ArrayList<Libro>
var mapa = new HashMap<String, Socio>(); // infiere HashMap<String, Socio>
```

No significa que Java sea dinámico: el tipo se fija en compilación, solo que no hay que escribirlo.

---

### Jerarquía de herencia en colecciones

```
Iterable
└── Collection
    ├── List
    │   ├── ArrayList
    │   └── LinkedList
    ├── Set
    │   ├── HashSet
    │   └── TreeSet (ordenado)
    └── Queue
Map (no hereda de Collection)
    ├── HashMap
    └── TreeMap (ordenado)
```

---

## 3. Genéricos y Excepciones

### Genéricos
Permiten escribir código que funciona con cualquier tipo, manteniendo la seguridad del compilador.

#### Clases genéricas
```java
public class Caja<T> {
    private T contenido;
    public void guardar(T contenido) { this.contenido = contenido; }
    public T obtener() { return contenido; }
}

Caja<Libro> cajaLibro = new Caja<>();
cajaLibro.guardar(new Libro(...));
Libro libro = cajaLibro.obtener(); // no necesita cast
```

#### Interfaces genéricas
En BiblioTech, `Repository<T, ID>` es una interfaz genérica. Sin genéricos habría que escribir una interfaz distinta para cada tipo, o usar `Object` con casts peligrosos.

```java
// T = Libro, ID = String
public class LibroRepositoryImpl implements Repository<Libro, String> { ... }

// T = Socio, ID = Integer
public class SocioRepositoryImpl implements Repository<Socio, Integer> { ... }
```

---

### Excepciones

#### Jerarquía de herencia
```
Throwable
├── Error (no manejar: OutOfMemoryError, StackOverflowError)
└── Exception
    ├── RuntimeException (unchecked)
    │   ├── NullPointerException
    │   ├── IllegalArgumentException
    │   └── BibliotecaException ← nuestra base
    └── IOException (checked)
```

#### Tipos de excepciones
**Checked:** heredan de `Exception`. El compilador obliga a manejarlas. Representan errores recuperables esperados.

**Unchecked:** heredan de `RuntimeException`. No obligan a manejarlas. En BiblioTech usamos esta base para que `orElseThrow` funcione correctamente con lambdas.

#### Bloques try-catch, finally, resource

```java
// try-catch
try {
    prestamoService.realizarPrestamo(isbn, socioId);
} catch (LibroNoDisponibleException e) {
    System.out.println("El libro no está disponible: " + e.getMessage());
} catch (LimitePrestamosException e) {
    System.out.println("Límite alcanzado: " + e.getMessage());
}

// finally: se ejecuta siempre
try {
    // código
} catch (Exception e) {
    // manejo
} finally {
    scanner.close(); // siempre se ejecuta
}

// try-with-resources: cierra el recurso automáticamente
try (Scanner sc = new Scanner(System.in)) {
    String linea = sc.nextLine();
} // sc.close() se llama automáticamente
```

#### Multi-catch
```java
try {
    // código
} catch (LibroNoEncontradoException | SocioNoEncontradoException e) {
    System.out.println("Recurso no encontrado: " + e.getMessage());
}
```

#### Excepciones propias
```java
public class BibliotecaException extends RuntimeException {
    public BibliotecaException(String mensaje) { super(mensaje); }
}

public class LibroNoDisponibleException extends BibliotecaException {
    public LibroNoDisponibleException(String isbn) {
        super("El libro con ISBN " + isbn + " no está disponible.");
    }
}
```

---

## 4. Streams

Los Streams permiten procesar colecciones de forma declarativa. En lugar de decir *cómo* hacerlo (bucle), decís *qué* querés hacer.

```java
// Sin streams: imperativo
List<Libro> resultado = new ArrayList<>();
for (Libro l : libros) {
    if (l.categoria().equals("Ficción")) resultado.add(l);
}

// Con streams: declarativo
List<Libro> resultado = libros.stream()
    .filter(l -> l.categoria().equals("Ficción"))
    .toList();
```

### forEach
Ejecuta una acción por cada elemento. Operación terminal (consume el stream).

```java
libros.forEach(System.out::println);
```

### map y collect
`map` transforma cada elemento. `collect` acumula los resultados.

```java
List<String> titulos = libros.stream()
    .map(Libro::titulo)
    .collect(Collectors.toList());

// Forma corta (Java 16+)
List<String> titulos = libros.stream()
    .map(Libro::titulo)
    .toList();
```

### peek
Ejecuta una acción sin modificar el elemento. Útil para depurar.

```java
libros.stream()
    .peek(l -> System.out.println("Procesando: " + l.titulo()))
    .filter(Libro::disponible)
    .toList();
```

### filter
Filtra elementos según una condición.

```java
// En BiblioTech, búsqueda por autor:
storage.values().stream()
    .filter(l -> l.autor().toLowerCase().contains(autor.toLowerCase()))
    .toList();
```

### findFirst
Devuelve el primer elemento que cumple la condición, envuelto en `Optional`.

```java
Optional<Socio> socio = storage.values().stream()
    .filter(s -> s.getDni().equals(dni))
    .findFirst();
```

### Operaciones varias

```java
// Contar
long disponibles = libros.stream().filter(Libro::disponible).count();

// Verificar condición
boolean hayFiccion = libros.stream()
    .anyMatch(l -> l.categoria().equals("Ficción"));

// Ordenar
List<Libro> ordenados = libros.stream()
    .sorted(Comparator.comparing(Libro::titulo))
    .toList();
```

### Reducción
Combina todos los elementos en un único resultado.

```java
int sumaAnios = libros.stream()
    .mapToInt(Libro::anio)
    .sum();

Optional<Integer> producto = List.of(1, 2, 3, 4).stream()
    .reduce((a, b) -> a * b); // 24
```

### Agrupadas
Agrupa elementos por un criterio en un `Map`.

```java
Map<String, List<Libro>> porCategoria = libros.stream()
    .collect(Collectors.groupingBy(Libro::categoria));

porCategoria.get("Ficción"); // todos los libros de Ficción
```

### flatMap
Aplana streams de streams en un único stream.

```java
List<List<String>> listas = List.of(List.of("a", "b"), List.of("c", "d"));

List<String> plana = listas.stream()
    .flatMap(Collection::stream)
    .toList(); // ["a", "b", "c", "d"]
```

---

## 5. Streams de java.io

Los streams de `java.io` son para leer y escribir datos en archivos o redes, distintos a los Streams de colecciones.

### Stream de bytes
Trabajan con datos binarios (imágenes, archivos comprimidos).

```java
try (FileInputStream fis = new FileInputStream("archivo.bin")) {
    int b;
    while ((b = fis.read()) != -1) {
        System.out.print(b);
    }
}
```

### Stream de caracteres
Trabajan con texto, manejan la codificación automáticamente.

```java
try (BufferedReader br = new BufferedReader(new FileReader("archivo.txt"))) {
    String linea;
    while ((linea = br.readLine()) != null) {
        System.out.println(linea);
    }
}
```

### Familia de clases

| Tipo | Entrada | Salida |
|---|---|---|
| Bytes | `InputStream`, `FileInputStream` | `OutputStream`, `FileOutputStream` |
| Caracteres | `Reader`, `FileReader`, `BufferedReader` | `Writer`, `FileWriter`, `BufferedWriter` |

Siempre usar `Buffered` para mejor rendimiento: lee bloques en lugar de byte por byte.

### NIO y Path (Java 7+)
API moderna para manejo de archivos, más simple y robusta.

```java
Path ruta = Path.of("datos/libros.txt");
```

### Files
Clase de utilidades para operaciones comunes con archivos.

```java
// Leer todas las líneas
List<String> lineas = Files.readAllLines(Path.of("libros.txt"));

// Escribir texto
Files.writeString(Path.of("salida.txt"), "contenido");

// Verificar existencia
Files.exists(Path.of("libros.txt"));

// Crear directorio
Files.createDirectories(Path.of("datos/backup"));
```

### Texto y binario con NIO

```java
// Leer archivo de texto completo
String contenido = Files.readString(Path.of("libros.txt"));

// Leer archivo binario
byte[] bytes = Files.readAllBytes(Path.of("imagen.png"));

// Escribir archivo binario
Files.write(Path.of("copia.png"), bytes);
```

Para el bonus de persistencia del TP se podría usar `Files.writeString` para guardar datos en CSV y `Files.readAllLines` para cargarlos al iniciar el sistema.