## 1. Purpose of This Document
- **Audience (humans + AI agents):** Apply these rules before writing, reviewing, or generating code; no exceptions for automation.
- **Why consistency matters in an AI-assisted codebase:** Consistent patterns keep AI outputs predictable, minimize merge conflicts, and allow humans to reason about fast-changing code.

## 2. Core Engineering Principles
- **Readability > cleverness:** DO choose the clearest construct even if it is longer; DO NOT introduce indirection without proven value.
- **Explicitness > implicit behavior:** DO wire dependencies and configurations transparently; DO NOT rely on magic defaults unless this file says so.
- **Stability > novelty:** DO reuse established components; DO NOT adopt experimental libraries without lead approval and rollout plan.
- **Fail fast, fail clearly:** DO validate inputs early and throw descriptive exceptions; DO NOT swallow errors or log without action.

## 3. Java Language Standards (Java 25-First)
- **Records as default for immutable data carriers:** DO model read-only DTOs and value objects as `record`; the older `@Value` + Lombok classes are deprecated unless accessor customization is required.
- **Sealed classes/interfaces for domain modeling:** DO seal inheritance hierarchies representing finite state (e.g., `sealed interface Payment permits CardPayment, WireTransfer`); DO NOT expose open inheritance unless extensibility is mandatory.
- **Pattern matching (instanceof, switch) usage rules:** DO prefer pattern matching `instanceof`/`switch` for type-safe decomposition; DO NOT mix with manual casts once Java 25 patterns cover the case.
- **Enhanced switch expressions (exhaustiveness required):** DO use switch expressions with exhaustive handling and `default` that throws; the older if/else chains are only acceptable when conditions are non-mutually-exclusive.
- **Scoped values vs ThreadLocal:** DO use scoped values for request or job context propagation; DO NOT add new `ThreadLocal` unless thread affinity is unavoidable and documented.
- **Virtual threads usage rules:** DO run blocking I/O workloads on virtual threads when latency benefits are measurable; DO NOT use virtual threads for CPU-bound or thread-unsafe libraries.
- **Immutability by default:** DO make fields `final` and prefer immutable collections; DO NOT expose setters on shared models without justification.
- **Collections usage (List vs Set vs Map rules):** DO pick `List` for ordered duplicates, `Set` for uniqueness enforcement, and `Map` when the key carries semantics; DO NOT expose raw collection types.
- **Streams vs loops (performance & readability tradeoffs):** DO use streams for declarative transformations with clear terminal operations; DO NOT chain streams when loops express the logic more plainly or profiling shows overhead.
- **Optional rules (return types only, never fields):** DO return `Optional` from methods signaling absence; DO NOT store `Optional` in records, entities, or fields.
- **Exception hierarchy & checked vs unchecked strategy:** DO use custom checked exceptions for recoverable domain errors and unchecked for programmer bugs; DO NOT wrap exceptions blindly—preserve cause chains.
- **Logging standards:** DO use `org.slf4j.Logger` via Lombok’s `@Slf4j` or explicit logger, log structured key-value data, and never log secrets; DO NOT rely on `System.out` or custom logging abstractions.

## 4. Java 25 Feature Usage Guidelines
- **When each major feature SHOULD be used:** DO adopt records, pattern matching, sealed types, scoped values, virtual threads, string templates, unnamed variables/patterns, and enhanced switch when they simplify reasoning.
- **When it MUST NOT be used:** DO NOT apply these features when they obscure intent, increase startup cost, or conflict with security reviews (e.g., string templates with untrusted input).
- **Anti-patterns involving new language features:** DO NOT create deep sealed hierarchies that mirror database enums; DO NOT allocate virtual threads inside tight loops without pooling.
- **Migration-safe design choices:** DO keep APIs backward compatible by providing transitional constructors or adapters when replacing classes with records; document any feature flagging strategy.

## 5. Object & API Design
- **Constructor vs factory vs builder rules:** DO use constructors/records for simple aggregates, static factories for named creation or validation, and builders only for 4+ optional parameters; DO NOT expose multiple public constructors with overlapping signatures.
- **Visibility and encapsulation:** DO keep fields private and expose intent via methods; DO NOT leak mutable internals or package-private APIs across modules.
- **Equality, hashCode, toString standards:** DO rely on record-generated implementations or `Objects` helpers; DO NOT hand-roll unless behavior differs (e.g., insensitive comparison) and is tested.
- **Null-handling strategy:** DO accept and return non-null values by default; use `@NonNull` tooling. DO validate external inputs and convert nullability to `Optional` or domain defaults immediately.

## 6. Spring Boot Architecture Rules
- **Layered architecture enforcement:** DO maintain controller → service → repository boundaries; DO NOT let controllers call repositories directly.
- **Controller responsibilities (and limits):** DO keep controllers thin, performing only request mapping, validation, and hand-off; DO NOT embed business logic or persistence code.
- **Service layer transaction boundaries:** DO annotate service methods with `@Transactional` when they mutate persistent state; DO NOT manage transactions manually unless unavoidable.
- **Repository rules (JPA or otherwise):** DO extend Spring Data repositories or use dedicated DAO classes; DO NOT execute ad-hoc SQL in services.
- **DTO ↔ Entity mapping rules:** DO isolate mapping via mappers (MapStruct/manual) and never expose entities to API consumers; DO NOT let DTO setters alter persistent entities directly.
- **Bean scope and lifecycle awareness:** DO prefer singleton beans; use prototype or request scope only with documented reasons and thread-safety proof.

## 7. Spring Boot 4.0 Best Practices
- **Updated defaults and breaking changes:** DO rely on Spring Boot 4 autoconfiguration but review release notes for removed properties; DO NOT reintroduce deprecated `WebMvcConfigurerAdapter` or legacy actuator endpoints.
- **Deprecated patterns to avoid:** DO NOT use `RestTemplate` for new code—prefer `WebClient`; DO NOT rely on `@EnableWebMvc` unless customizing the MVC stack manually.
- **Observability, health checks, and metrics:** DO wire Micrometer metrics, liveness/readiness probes, and distributed tracing; DO NOT disable actuator endpoints without alternative monitoring.

## 8. API Design Standards
- **REST conventions:** DO follow resource-based URIs, plurals, and standard HTTP verbs; DO NOT tunnel actions through query params.
- **Versioning strategy:** DO version APIs via URI prefix (`/v1/...`) or media type; DO NOT break existing versions without deprecation notice.
- **Error response format:** DO return a structured problem detail (HTTP status, code, message, correlation id); DO NOT expose stack traces in responses.
- **Validation and constraint handling:** DO use Bean Validation annotations and handle `MethodArgumentNotValidException` centrally; DO NOT skip validation for internal endpoints.

## 9. Security Guidelines
- **Authentication vs authorization boundaries:** DO integrate authentication at the gateway or filter level and enforce authorization within services; DO NOT mix both concerns inside controllers.
- **Input validation and deserialization safety:** DO configure Jackson with safe defaults (fail on unknown, limit polymorphic types) and validate payload sizes; DO NOT enable default typing.
- **Secrets and configuration handling:** DO source secrets from environment or vault providers, not from the repo; DO NOT log secrets or commit `.env` files.
- **Spring Security guardrails:** DO centralize security config, use `SecurityFilterChain`, and test custom voters; DO NOT bypass security for tests without `@WithMockUser` or equivalent safeguards.

## 10. Testing Strategy
- **Unit vs integration tests:** DO target 80%+ critical path coverage with unit tests and cover service/controller wiring with integration tests; DO NOT duplicate the same scenario at multiple levels.
- **Testcontainers usage:** DO spin up Testcontainers for database, message brokers, or external dependencies; DO NOT mock persistence layers when behavior matters.
- **What must always be tested:** DO test domain rules, error handling, serialization, and security-critical flows; DO ensure new public APIs ship with contract tests.
- **Mocking rules:** DO mock external systems and time-sensitive collaborators; DO NOT mock value objects or data repositories when real implementations are inexpensive.

## 11. Maven Project Standards
- **Dependency management:** DO keep versions centralized in the parent BOM and avoid direct version overrides; DO NOT add snapshot repositories without owner approval.
- **Plugin usage:** DO configure compiler, surefire, failsafe, jacoco, and formatting plugins; DO NOT disable warnings or skip tests by default.
- **Build reproducibility and CI safety:** DO pin toolchain versions (Java 25, Maven wrapper) and ensure builds run with `./mvnw verify`; DO NOT require local secrets or manual steps.

## 12. AI-Specific Rules (Non-Negotiable)
- **How AI agents must reason before coding:** DO read existing files, confirm requirements, and outline the plan before modifying code; DO NOT infer missing context without verification.
- **Rules against over-engineering:** DO select the simplest solution that meets requirements; DO NOT introduce new layers/patterns unless requested or justified in comments.
- **Rules for modifying existing code:** DO touch only relevant sections, preserve formatting, and mention any assumptions in PR descriptions; DO NOT refactor unrelated modules opportunistically.
- **When AI must ask for clarification instead of guessing:** DO pause and request guidance when requirements conflict, when architectural decisions are undefined, or when security implications are unclear.

## 13. Pull Request Review Checklist
- **Java correctness:** DO verify language features compile on Java 25 and nullability contracts hold.
- **Java 25 feature usage sanity:** DO confirm modern features replace older idioms meaningfully and avoid novelty for its own sake.
- **Spring correctness:** DO ensure bean wiring, configuration properties, and transactional boundaries are intact.
- **Performance implications:** DO review memory/CPU impact, virtual thread usage, and data access patterns.
- **Security concerns:** DO check authentication/authorization flows, secret handling, and validation.
- **Tests and documentation:** DO require updated tests, docs, and any migration notes before merge.

