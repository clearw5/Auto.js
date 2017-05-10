

module.exports = function(__runtime__, scope){

    scope.SetScreenMetrics = function(w, h){
        __runtime__.getRootShell().SetScreenMetrics(w, h);
    }

    scope.Tap = function(x, y){
        __runtime__.getRootShell().Tap(x, y);
    }

    scope.Swipe = function(x1, y1, x2, y2, duration){
        if(arguments.length == 5){
            __runtime__.getRootShell().Swipe(x1, y1, x2, y2, duration);
        }else{
            __runtime__.getRootShell().Swipe(x1, y1, x2, y2);
        }
    }

    scope.Screencap = function(path){
         __runtime__.getRootShell().Screencap(path);
    }

    scope.KeyCode = function(keyCode){
        __runtime__.getRootShell().KeyCode(keyCode);
    }

    scope.Home = function(){
        return KeyCode(3);
    }

    scope.Back = function(){
        return KeyCode(4);
    }

    scope.Power = function(){
        return KeyCode(26);
    }

    scope.Up = function(){
        return KeyCode(19);
    }

    scope.Down = function(){
        return KeyCode(20);
    }

    scope.Left = function(){
        return KeyCode(21);
    }

    scope.Right = function(){
        return KeyCode(22);
    }

    scope.OK = function(){
        return KeyCode(23);
    }

    scope.VolumeUp = function(){
        return KeyCode(24);
    }

    scope.VolumeDown = function(){
        return KeyCode(25);
    }

    scope.Menu = function(){
        return KeyCode(1);
    }

    scope.Camera = function(){
        return KeyCode(27);
    }

    scope.Text = function(text){
         __runtime__.getRootShell().Text(text);
    }

    scope.Input = scope.Text;

    return function(cmd, root){
       root = root ? 1 : 0;
       return __runtime__.shell(cmd, root);
   };
}
