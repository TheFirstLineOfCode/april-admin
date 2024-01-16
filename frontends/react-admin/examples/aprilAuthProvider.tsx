const authProvider = {
	login: (criterials) => {
		let usernameAndPassword = criterials.username + ':' + criterials.password;
		let authCode = 'Basic ' + btoa(usernameAndPassword);
		
		return fetch(`${serviceUrl}/auth`, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': authCode
			}
        }).then((response) => {
			if (response.status < 200 || response.status >= 300) {
				throw new Error(response.status);
			}
			
			localStorage.setItem('auth-code', authCode);
			return Promise.resolve('/')
		});
	},
	logout: () => {
		localStorage.removeItem('auth-code');
		return Promise.resolve('/login');
	},
	checkError: (error) => {
		const status = error.status;
		if (status === 401 || status === 403) {
			localStorage.removeItem('token');
			return Promise.reject();
		}
		// other error code (404, 500, etc): no need to log out
		return Promise.resolve();
	},
	checkAuth: () => {
		return localStorage.getItem('auth-code') ?
			Promise.resolve() :
			Promise.reject();
	},
	getPermissions: () => {
		// get user permissions logic here
	},
};

export default authProvider;