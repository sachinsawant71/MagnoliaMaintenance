'use strict';

// angular.js main app initialization
var app = angular.module('magnolia', ['ngRoute','magnolia.services','ui.bootstrap','naturalSort','ui.tinymce','ui.bootstrap.alerts']);

//Define Routing for app
app.config(['$routeProvider', function ($routeProvider) {
      $routeProvider.
        when('/', { templateUrl: 'pages/index.html', activetab: 'projects', controller: HomeCtrl }).
        when('/project/:projectId', {
          templateUrl: function (params) { return 'pages/' + params.projectId + '.html'; },
          controller: ProjectCtrl,
          activetab: 'projects'
        }).
        when('/contracts', {
          templateUrl: 'pages/contracts.html',
          controller: PrivacyCtrl,
          activetab: 'contracts'
        }).
        when('/documents', {
          templateUrl: 'pages/documents.html',
          controller: AboutCtrl,
          activetab: 'documents'
        }).
        when('/payments', {
          templateUrl: 'pages/payments.html',
          controller: PaymentsCtrl,
          activetab: 'payments'
        }).
        otherwise({ redirectTo: '/' });
    }]).run(['$rootScope', '$http', '$browser', '$timeout', "$route", function ($scope, $http, $browser, $timeout, $route) {

        $scope.$on("$routeChangeSuccess", function (scope, next, current) {
          $scope.part = $route.current.activetab;
        });

        // onclick event handlers
        $scope.showForm = function () {
          $('.contactRow').slideToggle();
        };
        $scope.closeForm = function () {
          $('.contactRow').slideUp();
        };

        // save the 'Contact Us' form
        $scope.save = function () {
          $scope.loaded = true;
          $scope.process = true;
          $http.post('sendemail.php', $scope.message).success(function () {
              $scope.success = true;
              $scope.process = false;
          });
        };
  }]);

app.config(['$locationProvider', function($location) {
    $location.hashPrefix('!');
}]);

app.directive('capitalize', function() {
   return {
     require: 'ngModel',
     link: function(scope, element, attrs, modelCtrl) {
        var capitalize = function(inputValue) {
		   if (inputValue) {
			   var capitalized = inputValue.toUpperCase();
			   if(capitalized !== inputValue) {
				  modelCtrl.$setViewValue(capitalized);
				  modelCtrl.$render();
				}         
				return capitalized;
		   }

         }
         modelCtrl.$parsers.push(capitalize);
         capitalize(scope[attrs.ngModel]);  // capitalize initial value
     }
   };
});

app.directive('printDiv', function ($document) {
  return {
    restrict: 'A',
    link: function(scope, element, attrs) {
      element.bind('click', function(evt){    
        evt.preventDefault();    
		console.log(angular.element('.' +attrs.printDiv).html());
        PrintElem(angular.element('.' +attrs.printDiv));
      });

      function PrintElem(elem) {
        PrintWithIframe($(elem).html());
      }

      function PrintWithIframe(data) 
      {
        if ($('iframe#printf').size() == 0) {
          $('html').append('<iframe id="printf" name="printf"></iframe>');  // an iFrame is added to the html content, then your div's contents are added to it and the iFrame's content is printed

          var mywindow = window.frames["printf"];
          mywindow.document.write('<html><head><title></title><link href="css/gridforms.css" rel="stylesheet" type="text/css" /><style>@page {margin: 25mm 0mm 25mm 5mm}</style>'
                          + '</head><body><div>'
                          + data
                          + '</div></body></html>');

          $(mywindow.document).ready(function(){
            mywindow.print();
            setTimeout(function(){
              $('iframe#printf').remove();
            },
            2000);  // The iFrame is removed 2 seconds after print() is executed, which is enough for me, but you can play around with the value
          });
        }

        return true;
      }
    }
  };
});