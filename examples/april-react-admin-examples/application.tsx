import Button from '@mui/material/Button';
import {fetchUtils} from 'react-admin';
import {AboutView} from './AboutView'
import {TestDataView} from './TestDataView'
import {UserListView} from './UserListView'
import {PostListView} from './posts/PostListView'
import {PostShowView} from './posts/PostShowView'
import {PostEditView} from './posts/PostEditView'
import {PostCreateView} from './posts/PostCreateView'
import {LoginPage} from './LoginPage'

export function customizeDataProvider(dataProvider) {
	dataProvider.loadTestData = function() {
		const url = `${serviceUrl}/test-data`;
		return httpClient(url, {method: 'POST'}).
			then(({json}) => {
				return {data: json};
			}
		);
	};
	
	dataProvider.clearTestData = function() {
		const url = `${serviceUrl}/test-data`;
		return httpClient(url, {method: 'DELETE'}).
			then(({json}) => {
				return {data: json};
			}
		);
	};
}

export function getApplicationViews() {
	const applicationViews = new Map([
		['TestDataView', TestDataView],
		['AboutView', AboutView],
		['UserListView', UserListView],
		['PostListView', PostListView],
		['PostShowView', PostShowView],
		['PostEditView', PostEditView],
		['PostCreateView', PostCreateView],
		['LoginPage', LoginPage]
	]);
	
	return applicationViews;
}
