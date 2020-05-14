import React from 'react';
import '../style/DonorIndividual.css';
import 'bootstrap/dist/css/bootstrap.min.css'

export default class DonorIndividual extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
		}
	}
	
	render() {
        return (
            <div className="donorInfo">
                <div classname="name">{this.props.donorName}</div>
				<div classname="email">{this.props.email}</div>
                <div>{this.props.amount}</div>
            </div>
        );
	}
}
