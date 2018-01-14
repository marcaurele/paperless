angular.module('repositories').component('repositories', {
	templateUrl : 'repository/repositories.html',
	controller : [ 'Repositories', function repositoriesController(Repositories) {
		this.repositories = Repositories.list();
	} ]
});