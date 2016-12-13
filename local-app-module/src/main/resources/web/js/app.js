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
      var port = ko.unwrap(bindingContext.$root.application.SocketIOPort);
      var hostname = ko.unwrap(bindingContext.$root.application.Hostname);

      var socketIOAddress = '//' + hostname + ':' + port;

      var socket = io.connect(socketIOAddress);

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
      var modules = viewModel.modules;

      for (var i = 0; i < modules().length; i++) {
        var settings = modules()[i].settings();

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

        modules()[i].shouldShowSave = ko.observable(shouldShowSave);
      }

      ko.applyBindings(viewModel, document.getElementById("cc-proxy"));
    });


  });

  window.save = function () {
    var rootElement = document.getElementById("cc-proxy")
    var viewModel = ko.dataFor(rootElement);

    var data = ko.mapping.toJS(viewModel.modules);

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
