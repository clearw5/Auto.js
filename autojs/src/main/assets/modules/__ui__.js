module.exports = function(__runtime__, scope){
    var ui = Object(__runtime__.ui);


    ui.layout = function(xml){
        view = ui.inflate(activity, xml);
        ui.setView(view);
    }

    ui.setView = function(view){
        ui.view = view;
        activity.setContentView(view);
    }

    ui.id = function(id){
        return ui.view.getChildAt(0).id(id);
    }

    return ui;
}
