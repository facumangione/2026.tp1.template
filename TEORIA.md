# Teoría del Proyecto BiblioTech

---

## Índice

1. Arquitectura en capas
2. Principios SOLID
3. Interfaces vs Clases Abstractas
4. Herencia en profundidad
5. Records
6. Optional
7. Excepciones Personalizadas
8. Repositorios e Inyección de Dependencias
9. Comandos de Git
10. Flujo de GitHub
11. Conventional Commits

---

## 1. Arquitectura en capas

El proyecto se divide en cuatro capas con responsabilidades bien definidas:

| Capa | Qué hace | Ejemplo |
|---|---|---|
| `model` | Representa los datos del sistema | `Libro`, `Socio` |
| `repository` | Guarda y busca datos | `LibroRepositoryImpl` |
| `service` | Aplica las reglas de negocio | `PrestamoService` |
| `Main` | Interactúa con el usuario | Menú por consola |

Separar en capas permite que si algo cambia (por ejemplo, pasar de memoria a base de datos), solo se toca la capa correspondiente sin romper el resto.

---

## 2. Principios SOLID

### S — Single Responsibility
Una clase tiene un solo motivo para cambiar. `Libro` representa datos, `LibroRepository` los guarda, `LibroService` aplica reglas. Ninguno hace el trabajo del otro.

### O — Open/Closed
El código está abierto para extensión, cerrado para modificación. Si aparece un nuevo tipo de recurso (Revista), se crea una clase nueva que implemente `Recurso`, sin tocar `Libro` ni `Ebook`.

### L — Liskov Substitution
Donde el código espera un `Socio`, debe funcionar igual con `SocioEstudiante` o `SocioDocente`. Ninguna subclase rompe el comportamiento esperado de la base.

### I — Interface Segregation
Las interfaces son pequeñas y específicas. `Repository<T, ID>` solo tiene `guardar`, `buscarPorId` y `buscarTodos`. No hay métodos que algunas implementaciones no necesiten.

### D — Dependency Inversion
Los servicios dependen de interfaces, no de implementaciones concretas. `PrestamoService` recibe un `Repository<Libro, String>`, no un `LibroRepositoryImpl`. Esto permite cambiar la implementación sin tocar el servicio.

---

## 3. Interfaces vs Clases Abstractas

**Interfaz:** define un contrato (qué métodos debe tener), sin implementación. Se usa cuando tipos muy distintos comparten capacidades mínimas.

```java
public interface Recurso {
    String isbn();   // Libro y Ebook deben tenerlo, pero son muy distintos
    String titulo();
}
```

**Clase abstracta:** puede tener implementación parcial. Se usa cuando varias clases comparten estructura y comportamiento, pero difieren en algunos detalles.

```java
public abstract class Socio {
    // Comportamiento común a todos los socios
    public boolean puedeTomarPrestado() {
        return !bloqueado && librosPrestados.size() < getLimitePrestamos();
    }

    // Cada subclase define su propio límite
    public abstract int getLimitePrestamos();
}
```

| | Interfaz | Clase Abstracta |
|---|---|---|
| ¿Tiene implementación? | No | Puede tener ambas |
| ¿Herencia múltiple? | Sí | No |
| ¿Cuándo usarla? | Tipos distintos con capacidades comunes | Tipos similares con comportamiento compartido |

En BiblioTech: `Recurso` es interfaz porque Libro y Ebook son muy distintos. `Socio` es clase abstracta porque Estudiante y Docente comparten nombre, DNI, email y toda la lógica de préstamos.

---

## 4. Herencia en profundidad

La herencia permite que una clase (hija) tome todo lo que tiene otra clase (padre) y le agregue o modifique comportamiento. Es una de las bases de la programación orientada a objetos.

### Cómo funciona

```java
public abstract class Socio {
    private final String nombre;  // campo heredado por todos

    public Socio(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; } // método heredado

    public abstract int getLimitePrestamos(); // obliga a cada hijo a implementarlo
}

public class SocioEstudiante extends Socio {

    public SocioEstudiante(String nombre) {
        super(nombre); // llama al constructor del padre
    }

    @Override
    public int getLimitePrestamos() {
        return 3; // implementación específica del hijo
    }
}
```

### La palabra clave `super`

`super` se usa para referirse a la clase padre. Tiene dos usos principales:

```java
// 1. Llamar al constructor del padre (siempre primera línea)
public SocioEstudiante(int id, String nombre, String dni, String email) {
    super(id, nombre, dni, email, CategoriaSocio.ESTUDIANTE);
}

// 2. Llamar a un método del padre desde el hijo
@Override
public String toString() {
    return super.toString() + " - Estudiante"; // usa el toString del padre y agrega
}
```

### La anotación `@Override`

Le dice al compilador que estás sobreescribiendo un método del padre. Si te equivocás en el nombre del método, el compilador te avisa. Es buena práctica usarla siempre.

```java
@Override
public int getLimitePrestamos() { // si escribís "getLimitePrestamo" (sin s), el compilador falla
    return 3;
}
```

### Polimorfismo

Es la capacidad de tratar objetos de distintos tipos de forma uniforme a través de la clase base. Es el resultado más poderoso de la herencia.

```java
// Este método no sabe si recibe un Estudiante o un Docente
public void mostrarLimite(Socio socio) {
    // getLimitePrestamos() ejecuta el método correcto según el tipo real
    System.out.println(socio.getNombre() + ": " + socio.getLimitePrestamos());
}

mostrarLimite(new SocioEstudiante(...)); // imprime 3
mostrarLimite(new SocioDocente(...));    // imprime 5
```

Java decide en tiempo de ejecución cuál versión del método llamar según el tipo real del objeto. Esto se llama **dynamic dispatch**.

### Cuándo usar herencia y cuándo no

Usá herencia cuando la relación es "es un":
- `SocioEstudiante` **es un** `Socio` ✅
- `Libro` **es un** `Recurso` ✅

No uses herencia cuando la relación es "tiene un":
- `PrestamoService` **tiene un** `Repository` → usá composición (campo en la clase)

Una regla práctica: si tenés que forzar la relación o la herencia te obliga a dejar métodos vacíos o lanzar excepciones en el hijo, probablemente no sea el diseño correcto.

---

## 5. Records

Un `record` es una clase inmutable diseñada para representar datos puros. Java genera automáticamente el constructor, getters, `equals`, `hashCode` y `toString`.

```java
// Equivalente a una clase con 30+ líneas
public record Libro(String isbn, String titulo, String autor, int anio) {}

// Uso
Libro libro = new Libro("978-...", "Clean Code", "Martin", 2008);
System.out.println(libro.titulo()); // getter sin "get"
```

Usá `record` cuando el objeto no cambia después de crearse (un Libro no muta). Usá clase cuando el objeto tiene estado variable (un Socio acumula préstamos).

---

## 6. Optional

`Optional<T>` evita devolver `null` en búsquedas. Obliga a manejar el caso donde no hay resultado.

```java
// Sin Optional: peligroso
Libro libro = repo.buscarPorIsbn("123"); // puede ser null
libro.getTitulo(); // NullPointerException si no existe

// Con Optional: seguro
Optional<Libro> resultado = repo.buscarPorIsbn("123");
Libro libro = resultado.orElseThrow(() -> new LibroNoEncontradoException("123"));
```

Métodos más usados:

| Método | Qué hace |
|---|---|
| `Optional.ofNullable(v)` | Crea un Optional que acepta null |
| `Optional.empty()` | Crea un Optional vacío |
| `isPresent()` | true si tiene valor |
| `orElse(x)` | Devuelve el valor o `x` si vacío |
| `orElseThrow(ex)` | Devuelve el valor o lanza la excepción |
| `ifPresent(accion)` | Ejecuta solo si tiene valor |

---

## 7. Excepciones Personalizadas

Permiten manejar cada tipo de error de forma específica y hacen el código más legible.

```java
// MAL: genérico, no se sabe qué falló
throw new Exception("Error");

// BIEN: el tipo ya dice qué pasó
throw new LibroNoDisponibleException(isbn);
```

La jerarquía del proyecto:
```
Exception
└── BibliotecaException           ← base de todos los errores de negocio
    ├── LibroNoDisponibleException
    ├── LibroNoEncontradoException
    ├── SocioNoEncontradoException
    └── LimitePrestamosException
```

Tener una base común permite un catch genérico si es necesario:

```java
try {
    servicio.realizarPrestamo(isbn, socioId);
} catch (LibroNoDisponibleException e) {
    System.out.println("Ese libro ya está prestado.");
} catch (LimitePrestamosException e) {
    System.out.println("Llegaste al límite de préstamos.");
} catch (BibliotecaException e) {
    System.out.println("Error: " + e.getMessage()); // cualquier otro error de negocio
}
```

---

## 8. Repositorios e Inyección de Dependencias

**Repositorio:** capa que abstrae el almacenamiento. El servicio no sabe si los datos están en memoria o en una base de datos.

```java
public interface Repository<T, ID> {
    void guardar(T entidad);
    Optional<T> buscarPorId(ID id);
    List<T> buscarTodos();
}
```

**Inyección de dependencias:** el servicio recibe sus repositorios por constructor, no los crea él mismo.

```java
// MAL: acoplado a la implementación concreta
public class PrestamoService {
    private LibroRepositoryImpl repo = new LibroRepositoryImpl();
}

// BIEN: depende de la interfaz, recibe lo que le pasen
public class PrestamoService {
    private final Repository<Libro, String> libroRepo;

    public PrestamoService(Repository<Libro, String> libroRepo) {
        this.libroRepo = libroRepo;
    }
}
```

---

## 9. Comandos de Git

Git es un sistema de control de versiones. Registra cada cambio que hacés en el código y te permite volver atrás, trabajar en paralelo y colaborar con otros.

### Configuración inicial

```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"
```

### Comandos básicos

| Comando | Qué hace |
|---|---|
| `git init` | Inicializa un repositorio en la carpeta actual |
| `git clone <url>` | Copia un repositorio remoto a tu máquina |
| `git status` | Muestra qué archivos cambiaron o están pendientes |
| `git add <archivo>` | Agrega un archivo al área de staging |
| `git add .` | Agrega todos los archivos modificados |
| `git commit -m "mensaje"` | Guarda los cambios con un mensaje descriptivo |
| `git log --oneline` | Muestra el historial de commits resumido |
| `git diff` | Muestra exactamente qué líneas cambiaron |

### Trabajar con ramas

| Comando | Qué hace |
|---|---|
| `git branch` | Lista todas las ramas locales |
| `git checkout -b nombre` | Crea una rama nueva y cambia a ella |
| `git checkout nombre` | Cambia a una rama existente |
| `git merge nombre` | Fusiona una rama en la rama actual |

### Trabajar con GitHub (remoto)

| Comando | Qué hace |
|---|---|
| `git remote add origin <url>` | Conecta el repo local con GitHub |
| `git push origin nombre-rama` | Sube una rama a GitHub |
| `git pull origin main` | Trae los cambios de GitHub a tu rama local |
| `git fetch` | Descarga cambios remotos sin aplicarlos |

### El área de staging

Git tiene tres zonas:

```
Directorio de trabajo  →  git add  →  Staging  →  git commit  →  Historial
(archivos modificados)                (listos)                    (guardados)
```

`git add` no guarda nada todavía, solo marca qué querés incluir en el próximo commit. Esto te permite commitear solo algunos archivos aunque hayas modificado varios.

### Errores comunes y cómo resolverlos

```bash
# "cambios locales serán sobrescritos por checkout"
# → Tenés archivos modificados sin commitear. Commiteá primero:
git add .
git commit -m "feat: work in progress"
git checkout otra-rama

# Ver qué archivos tienen cambios pendientes
git status

# Descartar cambios en un archivo (cuidado: no se puede deshacer)
git restore archivo.java
```

---

## 10. Flujo de GitHub

Cada funcionalidad sigue este ciclo:

```
1. Abrir Issue  →  describir la tarea antes de tocar código
2. Crear rama   →  git checkout -b feature/nombre (siempre desde main)
3. Escribir código y commitear archivo por archivo
4. Push         →  git push origin feature/nombre
5. Abrir PR     →  "Closes #N" vincula al Issue automáticamente
6. Revisar diff →  pestaña "Files changed"
7. Mergear      →  el Issue se cierra automáticamente
8. Volver a main→  git checkout main && git pull origin main
```

### Por qué siempre crear ramas desde main

Si creás una rama desde otra rama que no es main, arrastrás todos sus commits. Eso ensucia el historial del PR y mezcla cambios de distintas funcionalidades.

```bash
# SIEMPRE antes de crear una rama nueva:
git checkout main
git pull origin main       # asegurarse de tener main actualizado
git checkout -b feature/nueva-funcionalidad
```

---

## 11. Conventional Commits

Formato estándar para mensajes de commit:

```
tipo: descripción breve en minúsculas
```

### Por qué usar este formato

- El historial del proyecto se lee como una lista de cambios
- Es fácil entender qué hizo cada commit sin abrir el código
- Herramientas como el CHANGELOG se pueden generar automáticamente
- Es el estándar en la industria

### Tipos

| Tipo | Cuándo |
|---|---|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de bug |
| `refactor` | Cambio sin agregar ni corregir |
| `docs` | Solo documentación |
| `chore` | Configuración, dependencias |
| `test` | Agregar o modificar tests |

### Por qué `feat` en los títulos de Issues y PRs

Los Issues y PRs describen una funcionalidad completa, igual que un commit. Usar `feat:` en el título hace que sea consistente con los commits que contiene y que el profe pueda leer el historial de un vistazo y entender qué se hizo en cada PR.

```
PR #1  feat: modelos base del sistema
PR #2  feat: modelos de socios
PR #3  feat: jerarquía de excepciones
PR #4  feat: repositorios en memoria
PR #5  feat: servicios de negocio
PR #6  feat: interfaz de línea de comandos
```

### Ejemplos del proyecto

```bash
feat: add Recurso interface
feat: add Libro record
feat: add abstract Socio class with loan logic
feat: add SocioEstudiante with loan limit 3
feat: add BibliotecaException base class
fix: correct loan limit validation
docs: update CHANGELOG with sprint 3
chore: add .gitignore
```

Un buen mensaje responde a: "si aplico este commit, el proyecto va a..."
- "...agregar la interfaz Recurso" ✅
- "...cambios" ❌
- "...arreglé cosas" ❌