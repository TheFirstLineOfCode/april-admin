import {useEffect, useState} from 'react';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import {fetchUtils, useTranslate, useNotify, Title} from 'react-admin'
import {notifyFetchError} from './aprilAdmin'

const AboutDialog = ({showDialog, onClose}) => {
	const translate = useTranslate();
	const notify = useNotify();
	
	const [about, setAbout] = useState({
		applicationName: "Unknown application",
		version: "Unknown version",
		developer: "Unknown"
	});
	
	useEffect(() => {
		if (showDialog) {
			httpClient(`${serviceUrl}/about`).then(({json}) => {
				setAbout(json);
			}).catch(error => {
				notifyFetchError(notify, error);
			});	
		}
	}, [showDialog]);
	
	return (
		<Dialog open={showDialog} 
			aria-labelledby="alert-dialog-title"
			aria-describedby="alert-dialog-description">
			<DialogTitle id="alert-dialog-title">
				{translate('AboutView.about')}
			</DialogTitle>
			<DialogContent>
				<DialogContentText id="alert-dialog-description">
					<strong>{translate('AboutView.applicationName')}:</strong>{about.applicationName}
					<br/>
					<strong>{translate('AboutView.version')}:</strong> {about.version}
					<br/>
					<strong>{translate('AboutView.developer')}:</strong> {about.developer}
				</DialogContentText>
			</DialogContent>
			<DialogActions>
				<Button onClick={onClose}>{translate('AboutView.close')}</Button>
			</DialogActions>
		</Dialog>	
	)
}

export const AboutView = () => {
	const translate = useTranslate();
	const [showDialog, setShowDialog] = useState(false);
	
	return (
		<>
			<Title title="ca.title.about" />
			<Button variant="outlined" size="medium"
					sx= {{width: 128, padding: 1, margin: 2}}
						onClick={() => {setShowDialog(true);}}>
				{translate('AboutView.about')}
			</Button>
			
			<AboutDialog showDialog={showDialog} onClose={() => {setShowDialog(false);}} />
		</>
	)
}
