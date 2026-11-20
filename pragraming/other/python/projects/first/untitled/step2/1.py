fruits = ["سیب", "موز", "پرتقال"]

print(fruits[0])      # سیب
print(fruits[-1])     # پرتقال
print(len(fruits))    # 3

fruits.append("انگور")   # اضافه کردن به آخر
fruits.remove("موز")     # حذف کردن

for f in fruits:
    print(f)