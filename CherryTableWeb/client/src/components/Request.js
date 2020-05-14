import React from 'react';
import '../style/Request.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import PageNavbar from './PageNavbar';
import NewRequest from './NewRequest';
import RequestIndividual from './RequestIndividual';

export default class Request extends React.Component {
  constructor(props) {
    super(props);

    // The state maintained by this React Component. This component maintains the list of genres,
    // and a list of movies for a specified genre.
    this.state = {
      requests: []
    }
  }


  // React function that is called when the page load.
  componentDidMount() {
    let organization = "Childrens Hospital of Philadelphia"; //need to change to the logged in organization

    var fullUrl = window.location;
    var baseUrl = fullUrl.protocol + '//' + fullUrl.hostname.split(':')[0] + ':8080/' + fullUrl.pathname.split('/')[0];
    var reqUrl = baseUrl + "requests?organization=" + organization;
    console.log(reqUrl);

    fetch(reqUrl, {
      method: 'GET',
    }).then(res => {

      return res.json();
    }, err => {
      console.log(err);
    }).then (requests => {

      if (!requests) return;


      let requestDivs = requests.map((requestObj, i) =>
      <RequestIndividual
        key = {i}
        id={requestObj._id.toString()}
        title={requestObj.title}
        description={requestObj.description}
        target={requestObj.target}
        fulfilled={requestObj.fulfilled}
        donations={requestObj.donations.reduce((acc, currVal) => {
          return acc+"\n"+currVal;
        },"")}
        expand={false} />
      );

      this.setState({
        requests: requestDivs

      });
      console.log(this.state.requests)

    }, err => {
      console.log(err);
    });



  }


  /*

  componentDidMount() {
    let requests = [{ title: 'request 1', date: '2020-01-01', amount: '10000' },
                    { title: 'request 2', date: '2020-01-02', amount: '20000' }];
    let requests2 = [{id: "000000",
        title: 'fighting covid-19',
        donee: 'Children\'s Hospital of Philadelphia',
        donor: 'tony',
        date: '2020-01-01',
        amount: '10000',
        description: 'Masks for Children\'s Hospital of Philadelphia to fight covid-19'
      },
      { id: "000001",
        title: 'Go Quakers',
        donee: 'UPenn Hospital',
        donor: 'Lucy',
        date: '2020-01-01',
        amount: '10000',
        description: 'Donations for UPenn Hospital'
      }];
    let requestDivs = requests2.map((requestObj, i) =>
      <RequestIndividual key={i} title={requestObj.title} date={requestObj.date} amount={requestObj.amount} />
    );

    this.setState({
      requests: requestDivs
    });
  }
  */


  render() {
    return (
      <div className="Request">

        <PageNavbar active="request" />

        <br></br>
        <div className="container request-container">
          <br></br>
          <NewRequest expand={false} />
          <div className="jumbotron">
            <div className="request-container">
              <div className="request-header">
                <div className="header-lg"><strong>Request Name</strong></div>
                <div className="header"><strong>Target</strong></div>
                <div className="header"><strong>Fulfilled</strong></div>
              </div>
              <div className="results-container" id="results">
                {this.state.requests}
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
}
