

ui.layout = function(layout){
    layout = layout[0];
    view = ui.inflate(layout);
    ui.setView(view);
}

ui.inflate = function(xml){
    var name = xml.name();
    var view = ui.createView(name);
    ui.putAttributes(view, xml.attributes());
    ui.addChildren(view, xml.children());
    return view;
}

ui.putAttributes = function(view, attrs){
    var len = attrs.length();
    for(var i = 0; i < len; i++){
       var attr = attrs[i];
       view.putAttribute(attr.name(), attr.toString());
    }
}

ui.addChildren = function(view, children){
    var len = children.length();
    for(var i = 0; i < len; i++){
        var child = children[i];
        view.addChild(ui.inflate(child));
    }
}