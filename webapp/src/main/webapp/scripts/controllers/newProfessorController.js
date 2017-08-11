
angular.module('professor-service').controller('NewProfessorController', function ($scope, $location, locationParser, flash, ProfessorResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.professor = $scope.professor || {};
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The professor was created successfully.'});
            $location.path('/Professors');
        };
        var errorCallback = function(response) {
            if(response && response.data) {
                flash.setMessage({'type': 'error', 'text': response.data.message || response.data}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        ProfessorResource.save($scope.professor, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Professors");
    };
});