module.exports = function(__runtime__, scope){
    var ui = Object(__runtime__.ui);
    ui.layout = function(xml){
        view = ui.inflate(activity, xml);
        ui.setView(view);
    }

    ui.setView = function(view){
        activity.setContentView(view);
    }

    return ui;
}
