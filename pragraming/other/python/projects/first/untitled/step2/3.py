person = {
    "name": "علی",
    "age": 15,
    "city": "تهران"
}

print(person["name"])   # علی

person["age"] = 16      # تغییر مقدار
person["email"] = "ali@example.com"  # اضافه کردن کلید جدید

for key, value in person.items():
    print(key, ":", value)