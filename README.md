# Приложение на Spring Boot для имитации оплаты услуг мобильной связи

Напишите простое Spring Boot приложение, позволяющее имитировать оплату услуг мобильной связи. Предполагается, что в нем будет набор HTTP-методов, принимающих запросы из стороннего клиента (Postman, wget, curl и т.д.), какого-либо пользовательского web-интерфейса (UI на HTML и JS) в приложении не требуется.

Вся необходимая информация о пользователе хранится в БД. Для основной части это - id, логин, хэш пароля, баланс, произведенные оплаты (id, дата, номер телефона, сумма). Для дополнительной части еще понадобятся ФИО, email, пол и дата рождения.

## Основная часть

1. **Регистрация пользователя**:
   - Это единственный метод в приложении, доступный без авторизации. Все остальные HTTP-методы используют базовую авторизацию.
   - В качестве логина лучше взять номер телефона.
   - После регистрации на баланс пользователя зачисляется 1000 рублей, другие способы пополнения не предусмотрены.

2. **Получение баланса денежных средств**:
   - На основе авторизационных данных возвращается текущее значение баланса счета и логин пользователя.

3. **Оплата**:
   - Метод API должен принимать номер телефона, на который совершается оплата, и сумму (с копейками).
   - Если денег на счете пользователя достаточно, списываем их и возвращаем ответ, что оплата прошла успешно.
   - Сохраняем информацию о произведенной оплате в базу данных.

## Дополнительная часть

1. **Редактирование пользовательских данных**:
   - Пользователь должен иметь возможность создать/исправить свои ФИО, email, пол и дату рождения.

2. **История операций**:
   - Возвращать список оплат, выполненных пользователем (он - плательщик).
   - Должны возвращаться id операции, телефон, сумма, число.
   - Добавить пагинацию в этот метод. Условно, из имеющихся 35 записей, отдельно получать постранично 10 записей.

3. **Юнит-тесты**:
   - По реализованному функционалу должны быть написаны юнит-тесты.