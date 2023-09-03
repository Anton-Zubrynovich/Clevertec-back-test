# Clevertec-back-test
## Основые технологии
- Java 17
- Gradle
- PostgreSQL
- JDBC
- Lombok
## Инструкция по запуску
### Локальный запуск с помощью Gradle
> Требуется версия Java 17
- Клонировать репозиторий
```console
git clone https://github.com/Anton-Zubrynovich/Clevertec-back-test.git
```
* Создать PostgreSQL Database

```sql
CREATE DATABASE clevertec
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'English_World.1252'
    LC_CTYPE = 'English_World.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
```

* Установить переменные среды

```console
DB_URL - jdbc:postgresql://localhost:5432/clevertec
DB_USERNAME - postgres
DB_PASSWORD - username
```
* Создать таблицы в базе данных
  
```sql
CREATE TABLE IF NOT EXISTS public.account
(
    account_number bigint NOT NULL,
    balance numeric(20,2),
    customer_id bigint NOT NULL,
    bank_id bigint NOT NULL,
    CONSTRAINT account_pkey PRIMARY KEY (account_number),
    CONSTRAINT account_number_unique UNIQUE (account_number),
    CONSTRAINT account_customer_id_fkey FOREIGN KEY (customer_id)
        REFERENCES public.customer (customer_id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
        NOT VALID,
    CONSTRAINT fk_account_bank_id_fkey FOREIGN KEY (bank_id)
        REFERENCES public.bank (bank_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.account
    OWNER to postgres;

CREATE INDEX IF NOT EXISTS fki_account_bank_id_fk
    ON public.account USING btree
    (bank_id ASC NULLS LAST)
    TABLESPACE pg_default;

CREATE INDEX IF NOT EXISTS fki_account_customer_id_fkey
    ON public.account USING btree
    (customer_id ASC NULLS LAST)
    TABLESPACE pg_default;
```

```sql
CREATE TABLE IF NOT EXISTS public.bank
(
    bank_name character varying(30) COLLATE pg_catalog."default" NOT NULL,
    bank_id bigint NOT NULL DEFAULT nextval('bank_bank_id_seq'::regclass),
    CONSTRAINT bank_pkey PRIMARY KEY (bank_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.bank
    OWNER to postgres;
```

```sql
CREATE TABLE IF NOT EXISTS public.customer
(
    customer_id bigint NOT NULL DEFAULT nextval('user1_id_seq'::regclass),
    firstname character varying(30) COLLATE pg_catalog."default" NOT NULL,
    lastname character varying(30) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT customer_pkey PRIMARY KEY (customer_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.customer
    OWNER to postgres;
```

```sql
CREATE TABLE IF NOT EXISTS public.transactions
(
    transaction_id integer NOT NULL DEFAULT nextval('transactions_tran_id_seq'::regclass),
    date_of_transaction timestamp without time zone,
    transaction_type character varying(20) COLLATE pg_catalog."default",
    amount numeric(20,2),
    sender_account bigint NOT NULL DEFAULT 0,
    receiver_account bigint NOT NULL DEFAULT 0,
    CONSTRAINT transactions_transaction_id_pk PRIMARY KEY (transaction_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.transactions
    OWNER to postgres;
```

* Запустить Gradle

```console
gradle clean build bootRun
```

> Или же использовать gradle wrapper: `./gradlew`


## Описание проекта
В проекте была реализована схема работы приложения Clevertec-bank со следующими сущностями: Банк, Счёт, Пользователь, Транзакция.
Реализовани операции пополнения и снятия средств со счёта, а также возможность перевода средств другому клиенту Clever-Bank или же клиенту другого банка. При переводе средств в другой банк используется одна транзакция.
Раз полминуты, проверяется, нужно ли начислять проценты (1% - значение подставляется из конфигурационного файла config.yml) на остаток счёта в конце месяца.
Проверка и начисление процентов реализованы асинхронно.

Чеки о произведенных транзакциях сохраняются в папку check, в корне проекта формат .pdf.

Реализован механизм предоставления соединений с базой данных из пула соединений(класс ConnectionManager).


Для старта приложения используется класс MainPageUi в нем находится метод main(), запустив который, запускается метод startWork().
Далее нам предоставляется выбор: работать с вышеизложенным функционалом или же самим заполнить базу данных с помощью объектов реализовывающих интерфейс UtilUi, также имеющих метод startWork(). 
С помощью данных объектов мы можем взаимодействовать с базой данных через обекты пакета dao, которые имеют методы:

## AccountDao
- void save(Account account)
- void update(Account t, String[] params)
- void delete(Account t)
- Optional<Account> get(Long accountNumber)
- List<Account> getAll()
## BankDao
- void save(Bank bank)
- void update(Bank t, String[] params)
- void delete(Bank t)
- Optional<Bank> get(Long id)
- List<Bank> getAll()
## CustomerDao
- void save(Customer customer)
- void update(Customer t, String[] params)
- void delete(Customer t)
- Optional<Customert> get(Long accountNumber)
- List<Customer> getAll()

## TransactionDao
- void save(Transaction transaction)
- update(Transaction transaction, String[] params)
- delete(Transaction t)
- Optional<Transaction> get(Long id)
- List<Transaction> getAll()
- Optional<Transaction> getLast()
- void withdrawalTransaction(Account acc, double value)
- void depositTransaction(Account acc, double value)
- void transferTransaction(Account senderAccount, Account receiverAccount, double value)

  Данные объекты, в свою очередь, реализовывают интерфейс Dao<K, E>, имеющий следующие методы:
- Optional<E> get(K id)
- List<E> getAll()
- void save(E t)
- void update(E t, String[] params)
- void delete(E t)
- default void close(ResultSet rs, PreparedStatement pstmt, Connection con) -> данный метод передаёт соединение обратно в пул соединений.



Эти методы являются так называемыми CRUD-операциями, они в полной мере помогают выполнять действия по управлению счетами, клиентами, банками, транзакциями, удаляя, добавляя, изменяя объекты таблицы.








