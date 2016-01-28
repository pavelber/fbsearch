var app = angular.module('app', []);

app.filter('trustAsHtml', function ($sce) {
    return function (html) {
        return $sce.trustAsHtml(html);
    };
});

app.controller('SearchCtrl', ['$scope', '$http', '$location', function ($scope, $http, $location) {
    params = $location.search();
    $scope.url = '/search'; // The url of our search
    $scope.year = params['year'];
    $scope.keywords = params['keywords'];

   /* $http.get("/journals").
        success(function (data, status) {
            $scope.jorunalsstatus = status;
            $scope.journals = data;
            for (var i = 0; i < data.length; i++) {
                if (data[i].last != null) {
                    data[i].formateddate = moment(data[i].last).format("MMMM Do YYYY");
                }
                data[i].id = data[i].journal
            }
        })
        .
        error(function (data, status) {
            $scope.journals = data || "Request failed";
            $scope.jorunalsstatus = status;
        });

    $http.get("/news").
        success(function (data, status) {
            $scope.newsstatus = status;
            $scope.messages = data;

        })
        .
        error(function (data, status) {
            $scope.messages = data || "Request failed";
            $scope.newsstatus = status;
        });*/
    $http.get("/years").
        success(function (data, status) {
            $scope.yearsstatus = status;
            $scope.years = data;

        })
        .
        error(function (data, status) {
            $scope.years = data || "Request failed";
            $scope.yearsstatus = status;
        });

    // The function that will be executed on button click (ng-click="search()")
    $scope.search = function () {
        $scope.data = []
        // Create the http post request
        // the data holds the keywords
        // The request is a JSON request.
        $http.get($scope.url + "?term=" + $scope.keywords  +"&year="+$scope.year).
            success(function (data, status) {
                $location.search({
                    year: $scope.year,
                    keywords: $scope.keywords,
                });
                $scope.status = status;
                $scope.data = data;
                $scope.result = data; // Show result from server in our <pre></pre> element
                for (var i = 0; i < data.length; i++) {
                    if (data[i].date != null) {
                        data[i].formateddate = moment(data[i].date).format("MMMM Do YYYY");
                    }
                }
                if ( $scope.result.length ==0) {
                    $scope.message = "Your search did not match any documents. Search for 'жопа' instead";
                }else {
                    $scope.message ="";
                }
            })
            .
            error(function (data, status) {
                $scope.data = data || "Request failed";
                $scope.status = status;
            });
    };
}]);
