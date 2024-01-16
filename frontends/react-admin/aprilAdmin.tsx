import {
  Admin,
  Resource,
  ListGuesser,
  ShowGuesser,
  Layout,
  fetchUtils,
  HttpError,
  useLogout
} from "react-admin";

import simpleRestProvider from "ra-data-simple-rest";
import {TreeMenu} from "@bb-tech/ra-components"
import polyglotI18nProvider from 'ra-i18n-polyglot'
import en from './ca-language-english';
import cn from './ca-language-chinese';
import chineseMessages from './ca-language-chinese'
import authProvider from './aprilAuthProvider'
import {customizeDataProvider} from './application.tsx'

const i18nProvider = polyglotI18nProvider(
	locale => {
		if (locale === 'en') {
			return import('./ca-language-english').then(messages => messages.default);
		}
		
		return chineseMessages;
	},
	'cn',
	[
		{ locale: 'cn', name: '中文' },
		{ locale: 'en', name: 'English' }
	],
	{allowMissing: true}
);

async function fetchUiConfiguration(url, options = {}) {
	if (!options.headers) {
		options.headers = new Headers({Accept: 'application/json'});
	}
	
	const response = await fetch(url, options);
	const uiConfiguration = await response.json();
	
	return uiConfiguration;
}

async function getResources(serviceUrl, options = {}) {
	const uiConfigurationUrl = `${serviceUrl}/ui-configuration`;
	
	const uiConfiguration = await fetchUiConfiguration(uiConfigurationUrl, options);
	const resources = await uiConfiguration.resources;
	
	return resources;
}

export async function fetchAdminConfiguration(serviceUrl, options = {}) {
	const resources = await getResources(serviceUrl, options);
	const httpClient = (url, options = {}) => {
		if (!options.headers) {
			options.headers = new Headers({Accept: 'application/json'});
		}
		
		let authCode = localStorage.getItem('auth-code');
		if (authCode !== undefined) {
			options.headers.append("Authorization", authCode);
		}
		
		return fetchUtils.fetchJson(url, options);
    }
	
	window.httpClient = httpClient;
	
	const dataProvider = simpleRestProvider(serviceUrl, httpClient);
	
	window.serviceUrl = serviceUrl;
	const configuration = {
		"resources": resources,
		"dataProvider": dataProvider
	};
	
	return configuration;
}

function getResourceConfigurations(resources, applicationViews) {
	const resourceConfigurations = new Array();
	let index = 0;
	resources.map(resource => {
		let options = {
			"label": resource.label
		};
		
		if (resource.menuParent !== undefined)
			options['menuParent'] = resource.menuParent;
		
		let listComponent;
		if (resource.parentMenu) {
			options['isMenuParent'] = true;
		} else {
			if (resource.listViewName !== undefined) {
				listComponent = applicationViews.get(resource.listViewName);
			} else {
				listComponent = ListGuesser;
			}
		}
		
		let showComponent;
		if (!resource.parentMenu) {
			if (resource.showViewName !== undefined) {
				showComponent = applicationViews.get(resource.showViewName);
			} else {
				showComponent = ShowGuesser;
			}
		}
		
		let editComponent;
		if (!resource.parentMenu) {
			if (resource.editViewName !== undefined) {
				editComponent = applicationViews.get(resource.editViewName);
			}
		}
		
		let createComponent;
		if (!resource.parentMenu) {
			if (resource.createViewName !== undefined) {
				createComponent = applicationViews.get(resource.createViewName);
			}
		}
		
		let recordRepresentation;
		if (resource.recordRepresentation !== undefined) {
			recordRepresentation = resource.recordRepresentation;
		}
		
		const resourceConfiguration = {
			"name": resource.name,
			"options": options,
			"list": listComponent,
			"show": showComponent,
			"edit": editComponent,
			"create": createComponent,
			"recordRepresentation": recordRepresentation
		};
		
		resourceConfigurations[index++] = resourceConfiguration;
	});
	
	return resourceConfigurations;
}

export const AprilAdmin = ({configuration, applicationViews}) => {
	customizeDataProvider(configuration.dataProvider);
	
	const resourceConfigurations = getResourceConfigurations(configuration.resources, applicationViews);
	
	const LoginPage = applicationViews.get('LoginPage');
	
	return (
		<Admin loginPage={LoginPage} layout={AprilLayout} dataProvider={configuration.dataProvider} authProvider={authProvider} i18nProvider={i18nProvider}>
			{
				resourceConfigurations.map(resourceConfiguration => (
				<Resource
					key={resourceConfiguration.name}
					name={resourceConfiguration.name}
					recordRepresentation={resourceConfiguration.recordRepresentation}
					options={resourceConfiguration.options}
					list={resourceConfiguration.list}
					show={resourceConfiguration.show}
					edit={resourceConfiguration.edit}
					create={resourceConfiguration.create}
				/>))
			}
		</Admin>
	);
}

export const AprilLayout = (props) => {
	return (<Layout {...props} menu={TreeMenu} />);
}

export function notifyFetchError(notify, error) {
	if (error instanceof TypeError) {
		notify(`${error.message}.`, {type: 'error'});
	} else if (error instanceof HttpError) {
		notify(`${error.body.status} ${error.body.error}.`, {type: 'error'});
	} else {
		notify(`Unknown Error: ${error}.`, {type: 'error'});
	}
}

const LogoutButton = () => {
	const logout = useLogout();
	return (
		<button onClick = {logout}>Logout</button>
	);
};
