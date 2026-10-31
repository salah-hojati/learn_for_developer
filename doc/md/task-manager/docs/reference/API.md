---
title: "راهنمای نصب تفصیلی"
---

# راهنمای نصب تفصیلی

<div dir="rtl">

## پیش‌نیاز

- [ ] Node.js نصب شده
- [ ] Git نصب شده
- [ ] دسترسی به ترمینال

## نصب
</div>

```bash
git clone https://github.com/example/task-manager.git
cd task-manager
npm install

```http
POST /api/tasks
Content-Type: application/json
```
<div dir="rtl">

## ایجاد وظیفه
</div>

```http
POST /api/tasks
Content-Type: application/json
```
<div dir="rtl">

بدنه درخواست:
</div>

```json
{
  "title": "طراحی صفحه اصلی",
  "assignee": "علی",
  "priority": "high"
}
```

پاسخ موفق:

```json
{
  "id": 123,
  "status": "created",
  "created_at": "2026-10-22T10:00:00Z"
}
```

| کد وضعیت | توضیح |
|----------|-------|
| 201 | ایجاد شد |
| 400 | درخواست نامعتبر |
| 401 | احراز هویت نشده |


</div>