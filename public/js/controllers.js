'use strict';

function HomeCtrl($scope, $filter,Apartments) {
    $scope.items = [];

    Apartments.query(function(response) {
      $scope.items = response;	  
    });

	$scope.sortByAmount = function() {
		$scope.sortorder = "sortAmoutFunction";
	}

	$scope.sortAmoutFunction = function(item) {
		return -(item.maintenanceDetails.maintenanceDueTotal- item.maintenanceDetails.maintenancePaidTotal);
	};

	$scope.predicateBlueChip = $scope.natural('flatnumber');


    $scope.filterdata ="";
	$scope.addFilter = function(item) {

		if ($scope.filterdata==""){
			return true;
		}

		if (($scope.filterdata=="kyc-done") && (item.gasKYC=="1")){
			return true;
		}

		if (($scope.filterdata=="kyc-pending") && (item.gasKYC=="0")){
			return true;
		}

		if (($scope.filterdata=="owner") && (item.status=="1")){
			return true;
		}

		if (($scope.filterdata=="tenant") && (item.status=="2")){			
			return true;
		}

		if (($scope.filterdata=="empty") && (item.status=="0")){
			return true;
		}

		if (($scope.filterdata=="defaulter") && 
			(item.maintenanceDetails.maintenanceDueTotal-
				item.maintenanceDetails.maintenancePaidTotal > 0)){
			return true;
		}

		return false;
	}

	$scope.getClassNameForAmount = function(item) {
		var diff = item.maintenanceDetails.maintenanceDueTotal-
				item.maintenanceDetails.maintenancePaidTotal;
		if (diff > 0){
			return "amount-red";
		} 

		return "amount-green";
		
	};

	$scope.toggleItem = function(item) {
        $scope.active = item;
    };


};


function ProjectCtrl($scope,Apartments) {
	 $scope.products= Apartments.query();
}

function PrivacyCtrl($scope, Apartments) {
	$scope.products= Apartments.query();
}

function AboutCtrl($scope, $http, $timeout) {
}

function PaymentsCtrl($scope,Payments) {
	$scope.payments= Payments.query();

	$scope.removePayment = function (index,payment) {
        Payments.remove({id: payment._id}, function() {
        $scope.payments.splice(index, 1);
      });

	};

}

var ModalDemoCtrl = function ($scope, $modal, $log) {

  $scope.messageTemplates = [
        {"id":"Template1","template":"Dear Member, "}, 
        {"id":"Template2","template":"Dear Member,Template2 "}, 
        {"id":"Template3","template":"<strong>Dear Member</strong>,Template3 "}];

	$scope.open = function () {

    var modalInstance = $modal.open({
      templateUrl: 'sendMailModal',
      controller: ModalInstanceCtrl,
      resolve: {
        messageTemplates: function () {
          return $scope.messageTemplates;
        }
      }
    });

    modalInstance.result.then(function (selectedItem) {
	  console.log($scope.messageTemplates.selectedTemplate);  
    });
  };
};

var ModalInstanceCtrl = function ($scope, $modalInstance, messageTemplates) {

  $scope.messageTemplates = messageTemplates;
  $scope.messageText = 'test';

  $scope.selectTemplate = function (index) {
	  console.log('selected index' + index );
      $scope.messageTemplates.selectedTemplate = messageTemplates[index].template;
  };


  $scope.ok = function () {	
    console.log($scope.messageTemplates.selectedTemplate);
    $modalInstance.close($scope.messageTemplates.selectedTemplate);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
};


var ModalNewApartmentCtrl = function ($scope, $modal, $log) {
	
    $scope.alerts = [
        {"msg":"Maintenace Pending","type":"error"},
		 {"msg":"KYC Not done","type":"warning"}];


	$scope.open = function (item,index) {
		$scope.active = item;
		var modalInstance = $modal.open({
		  templateUrl: 'newApartmentModal',
		  controller: ModalInstanceNewApartmentCtrl,
		  windowClass : 'modal-huge',
		  resolve: {
			item: function () {
			  return item;
			}
		  }
		});

		modalInstance.result.then(function (selectedItem) {}, function () {
			$log.info(new Date());
		});
	};

};

var ModalInstanceNewApartmentCtrl = function ($scope, $modalInstance, item,$filter,Apartments,AlertService) {

  $scope.item = item;
  $scope.messageText = '';

  $scope.selectTemplate = function (index) {
      $scope.messageText = items[index].template;
  };

  $scope.resetTenantAddress = function() {
	$scope.item.owner.address = '';
	$scope.item.tenant = new Object();
	$scope.item.tenant.emails = new Array();
	$scope.item.tenant.phones = new Array();
  };


  $scope.ok = function () {
	var selectedApartment = Apartments.get({id:item.flatnumber},function(apartment) {
		apartment.gasKYC = item.gasKYC;
		apartment.status = item.status;
		apartment.propertyTax = item.propertyTax;
		apartment.owner.name = item.owner.name;
		apartment.owner.address = item.owner.address;
		apartment.owner.emails[0] = item.owner.emails[0];
		apartment.owner.emails[1] =  item.owner.emails[1];
		apartment.owner.phones[0] = item.owner.phones[0];
		apartment.owner.phones[1] =  item.owner.phones[1];

		if (item.status=="2"){		
			apartment.tenant=new Object();
			apartment.tenant.name = item.tenant.name;
			apartment.tenant.address = item.tenant.address;
			apartment.tenant.emails = new Array();
			apartment.tenant.emails[0] = item.tenant.emails[0];
			apartment.tenant.phones = new Array();
			apartment.tenant.phones[0] = item.tenant.phones[0];
			apartment.tenant.phones[1] = item.tenant.phones[1];
			apartment.tenant.policeverification = item.tenant.policeverification
			apartment.tenant.agreement = item.tenant.agreement
			apartment.tenant.registration =	item.tenant.registration
		}

		apartment.$save();
		AlertService.clear();
		AlertService.success('Information for apartment ' + item.flatnumber + ' is updated');
	});
    $modalInstance.close($scope.messageText);
  };

  $scope.cancel = function () {
	Apartments.get({id:item.flatnumber},function(apartment) {
		item.gasKYC = apartment.gasKYC;
		item.status = apartment.status;
		item.propertyTax = apartment.propertyTax;
		item.owner.name = apartment.owner.name;
		item.owner.address = apartment.owner.address;
		item.owner.emails[0] = apartment.owner.emails[0];
		item.owner.emails[1] =  apartment.owner.emails[1];
		item.owner.phones[0] = apartment.owner.phones[0];
		item.owner.phones[1] =  apartment.owner.phones[1];
		
	});
	$modalInstance.dismiss('cancel');
	
  };


  $scope.getClassName = function() {
	var diff = item.maintenanceDetails.maintenanceDueTotal-
			item.maintenanceDetails.maintenancePaidTotal;
	if (diff > 0){
		return "red";
	} 

	return "green";
	
  };


};




var ModalPaymentCtrl = function ($scope, $modal, $log) {

	$scope.open = function (item,index,payment) {
		$scope.active = item;
		var modalInstance = $modal.open({
		  templateUrl: 'paymentModal',
		  controller: ModalInstancePaymentCtrl,		
		  resolve: {
			item: function () {
			  return {"item" : item,"payment" : payment};
			}
		  }
		});

		modalInstance.result.then(function (selectedItem) {}, function () {
			$log.info(new Date());
		});
	};

};



var ModalInstancePaymentCtrl = function ($scope, $modalInstance, item,$filter,Apartments,Payments) {

  $scope.item = item.item;
  $scope.banks =  ['ICICI Bank','IDBI Bank','SBI Bank','Punjab National Bank','HDFC Bank','Saraswat Bank','Kotak Mahindra Bank','Yes Bank','Allahabad Bank',
	'Andhra Bank','Bank of Baroda','Bank of India', 'Bank of Maharastra','Bhartiya Mahila Bank','Canara Bank','Central Bank of India','Corporation Bank',
	'Dena Bank','Indian Bank','Indian Overseas Bank','Oriental Bank of Commerce','Punjab and Sind Bank','Syndicate Bank','UCO Bank','Union Bank of India',
	'United Bank of India','Vijaya Bank','State Bank of India','State Bank of Bikaner & Jaipur','State Bank of Hyderabad','State Bank of Mysore','State Bank of Patiala',
	'State Bank of Travancore','IndusInd Bank','ING Vysya Bank','Karnataka Bank','Karur Vysya Bank','Lakshmi Vilas Bank','Nainital Bank','Tamilnadu Mercantile Bank',
	'Dhanlaxmi Bank','Federal Bank','City Union Bank','Development Credit Bank','BNP Paribas','HSBC','Citibank N.A.'];

  if (!!item.payment) {
	$scope.payment = item.payment;	
	Apartments.get({id:item.payment.flatnumber},function(apartment) {
		$scope.item = apartment;		
	});	

	$scope.readonly = true;
	console.log($scope.payment);

  }else {
	$scope.payment = {};
	$scope.readonly = false;
	$scope.payment.paymentMode = "Check";
  } 

  $scope.ok = function () {	
    console.log($scope.payment);

	var payments = Payments.query(function () {
                       $scope.payments = payments;
            }, function ($http) {
                console.log('Couldn\'t get payment.');
            });
	
	var payment = new Payments({
                    date: new Date(),
					type: $scope.payment.type,					
					flatnumber: $scope.item.flatnumber,
                    email:$scope.item.owner.emails[0],
					mode: $scope.payment.mode,
					bank: $scope.payment.bank,
					amount : $scope.payment.amount					
             });

	if ($scope.payment.paymentType == 'Maintenance Payment') {
		payment.period = $scope.payment.period;
	}

    // Send POST request to the back end.
    payment.$save(function (data) {
                    payments.push(data);
                }, function ($http) {
                    console.log('Couldn\'t save payment.');
                });


	$modalInstance.close();
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };


 

};

