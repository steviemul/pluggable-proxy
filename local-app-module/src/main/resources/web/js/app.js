(function ($, ko, io) {

  ko.bindingHandlers['proxy-action'] = {
    init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {

      $(element).bind('click.proxy', function () {
        var input = {
          "actionName": viewModel.key(),
          "namespace": bindingContext.$parent.namespace()
        };

        $.post('/.action', JSON.stringify(input),
          function (response) {
            toastr.success('Action executed successfully','Success');
          },
          'json');
      });
    }
  };

  ko.bindingHandlers['proxy-fileTree'] = {
    update: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
      var id = bindingContext.$parent.id() + '-' + viewModel.key();

      $(element).fileTree({
          root: '/',
          script: '/.api/file?dirs=true',
          expandSpeed: 500,
          collapseSpeed: 500,
          multiFolder: false
        },
        function (file) {

        },
        function (dir) {
          viewModel.value(dir);
        }
      );

    }
  };

  ko.bindingHandlers['proxy-socket'] = {
    init: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
      var socket = io.connect('http://localhost:9091');

      socket.on('connect', function () {
        viewModel.messages.push("Successfully connected");
      });

      var evt = bindingContext.$parent.id() + '-networkevent';

      socket.on(evt, function (data) {
        viewModel.messages.push(data);
      });
    }
  };

  $(document).ready(function () {
    $.get("data", function (data) {
      var viewModel = ko.mapping.fromJS(data);

      for (var i = 0; i < viewModel().length; i++) {
        var settings = viewModel()[i].settings();

        var shouldShowSave = false;

        for (var j = 0; j < settings.length; j++) {
          var setting = settings[j];
          var type = setting.type();

          if (type != 'console') {
            shouldShowSave = true;
          }

          if (type == 'file') {
            setting.folderClass = ko.observable('glyphicon glyphicon-folder-close');
          }
        }

        viewModel()[i].shouldShowSave = ko.observable(shouldShowSave);
      }

      ko.applyBindings(viewModel, document.getElementById("cc-proxy"));
    });


  });

  window.save = function () {
    var rootElement = document.getElementById("cc-proxy")
    var viewModel = ko.dataFor(rootElement);

    var data = ko.mapping.toJS(viewModel);

    $.ajax({
      url: "data",
      type: 'PUT',
      data: JSON.stringify(data),
      success: function (response) {
        toastr.success('Settings updated successfully','Success');
      }
    });
  };

})(jQuery, ko, io);