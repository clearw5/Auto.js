console.show();

log("将产生5个1到100的随机数");

for(let i = 0; i < 5; i++){
    print(random(1, 100));
    print("  ");
    sleep(400);
}
print("\n");

log("将产生10个1到20的不重复随机数");

var exists = {};

for(let i = 0; i < 10; i++){
    var r;
    do{
        r = random(1, 20);
    }while(exists[r]);
    exists[r] = true;
    print(r + "  ");
    sleep(400);
}