resilience4j.circuitbreaker:
instances:
backendA:
registerHealthIndicator: true          # Регистрировать индикатор здоровья для мониторинга
slidingWindowSize: 100                 # Размер скользящего окна (количество вызовов или секунд)
backendB:
registerHealthIndicator: true          # Регистрировать индикатор здоровья
slidingWindowSize: 10                  # Размер скользящего окна
permittedNumberOfCallsInHalfOpenState: 3   # Разрешенное количество вызовов в полуоткрытом состоянии
slidingWindowType: TIME_BASED          # Тип окна: по времени (TIME_BASED) или по вызовам (COUNT_BASED)
minimumNumberOfCalls: 20               # Минимальное количество вызовов для расчета частоты отказов
waitDurationInOpenState: 50s           # Время ожидания в открытом состоянии перед переходом в полуоткрытое
failureRateThreshold: 50               # Порог частоты отказов в процентах (50% = замыкание)
eventConsumerBufferSize: 10            # Размер буфера событий для потребления
recordFailurePredicate: io.github.robwin.exception.RecordFailurePredicate  # Предикат для определения сбоя

resilience4j.retry:
instances:
backendA:
maxAttempts: 3                         # Максимальное количество попыток (включая первую)
waitDuration: 10s                      # Базовая задержка между попытками
enableExponentialBackoff: true         # Включить экспоненциальную задержку (удвоение)
exponentialBackoffMultiplier: 2        # Множитель для экспоненциальной задержки
retryExceptions:                       # Исключения, при которых выполняется повтор
- org.springframework.web.client.HttpServerErrorException
- java.io.IOException
ignoreExceptions:                      # Исключения, при которых повтор НЕ выполняется
- io.github.robwin.exception.BusinessException
backendB:
maxAttempts: 3                         # Максимальное количество попыток
waitDuration: 10s                      # Базовая задержка между попытками
retryExceptions:                       # Исключения для повтора
- org.springframework.web.client.HttpServerErrorException
- java.io.IOException
ignoreExceptions:                      # Исключения для игнорирования (без повтора)
- io.github.robwin.exception.BusinessException

resilience4j.bulkhead:
instances:
backendA:
maxConcurrentCalls: 10                 # Максимальное количество параллельных вызовов
backendB:
maxWaitDuration: 10ms                  # Максимальное время ожидания при занятости
maxConcurrentCalls: 20                 # Максимальное количество параллельных вызовов

resilience4j.thread-pool-bulkhead:
instances:
backendC:
maxThreadPoolSize: 1                        # Максимальный размер пула потоков
coreThreadPoolSize: 1                       # Основной размер пула потоков
queueCapacity: 1                            # Вместимость очереди перед пулом потоков
writableStackTraceEnabled: true             # Записывать стек вызовов для диагностики

resilience4j.ratelimiter:
instances:
backendA:
limitForPeriod: 10                     # Лимит запросов за период
limitRefreshPeriod: 1s                 # Период обновления лимита
timeoutDuration: 0                     # Таймаут ожидания разрешения (0 = без ожидания)
registerHealthIndicator: true          # Регистрировать индикатор здоровья
eventConsumerBufferSize: 100           # Размер буфера событий
backendB:
limitForPeriod: 6                      # Лимит запросов за период (6 вызовов)
limitRefreshPeriod: 500ms              # Период обновления лимита (0.5 секунды)
timeoutDuration: 3s                    # Таймаут ожидания разрешения (3 секунды)

resilience4j.timelimiter:
instances:
backendA:
timeoutDuration: 2s                    # Максимальная длительность выполнения (2 секунды)
cancelRunningFuture: true              # Отменять запущенную задачу при таймауте
backendB:
timeoutDuration: 1s                    # Максимальная длительность выполнения (1 секунда)
cancelRunningFuture: false             # НЕ отменять запущенную задачу при таймауте