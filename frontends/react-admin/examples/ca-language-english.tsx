import {TranslationMessages} from 'ra-core';
import englishMessages from 'ra-language-english';

export default {
	...englishMessages,
	ca: {
		menu: {
			tools: 'Tools',
			help: 'Help'
		},
		title: {
			users: 'Users',
			posts: 'Posts',
			testData: 'Test Data',
			about: 'About'
		}
	},
	AboutView: {
		about: 'About',
		applicationName: 'Application Name',
		version: 'Version',
		developer: 'Developer',
		close: 'CLOSE'
	},
	TestDataView: {
		totalUsers: 'Total Users',
		totalPosts: 'Total Posts',
		loadTestData: 'Load Test Data',
		clearTestData: 'Clear Test Data',
		loadingTestData: 'Loading....',
		cleaningTestData: 'Clearing....'
	}
}
