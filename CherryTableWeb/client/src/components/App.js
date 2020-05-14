import React from 'react';
import {
	BrowserRouter as Router,
	Route,
	Switch
} from 'react-router-dom';

import Home from './Home';
import Login from '../style/App.jsx'
import Request from './Request';
import Profile from './Profile';
import DonationStatus from './DonationStatus';
import MyDonors from './MyDonors';
import UserRoutes from './routes/userRoutes';
export default class App extends React.Component {

	render() {
		return (
			<div className="App">
				<Router>
					<Switch>
					<Route
							exact
							path="/"
							component={Login}
						/>
						
						<UserRoutes
							exact
							path="/Home"
							component={Home}
						/>
						<UserRoutes
							path="/Request"
							component={Request}
						/>
						<UserRoutes
							path="/Profile"
							component={Profile}
						/>
						/<UserRoutes
							path="/MyDonors"
							component={MyDonors}
						/>

						<UserRoutes 
						path="/DonationStatus"
						component={DonationStatus}
						/>
					</Switch>
				</Router>
			</div>
		);
	}
}