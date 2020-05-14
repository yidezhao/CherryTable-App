import React from 'react'
import '../style/NewRequest.css';
import { Formik, Form, Field, ErrorMessage } from 'formik'
import 'bootstrap/dist/css/bootstrap.min.css'

export default class NewRequest extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      expand: this.props.expand
    }
  }

  toggle() {

    this.setState({
      expand: !this.state.expand
    })
  }

  submit(values) {
    var fullUrl = window.location;
    var baseUrl = fullUrl.protocol + '//' + fullUrl.hostname.split(':')[0] + ':8080/' + fullUrl.pathname.split('/')[0];
    var reqUrl = baseUrl + 'newRequest?' +
      'title=' + values.title +
      '&desc=' + values.desc +
      '&target=' + values.target +
      '&organization=';
    console.log(reqUrl);
    fetch(reqUrl, {
      method: 'GET',
    }).then(response => {
      if (response.ok) {
        response.json()
        .then(json => {
          console.log(json);
          alert(JSON.stringify(json, null, 2));
        });
      } else {
        alert('Failed to submit new request.');
      }
    })
  }

  render() {
    if (!this.state.expand) {
      return (
        <div className='jumbotrongit'>
        <button type='button'
          className='btn btn-primary'
          onClick={() => {this.toggle()}}>New Request</button>
        </div>
      )
    } else {
      return (
        <div className='jumbotron'>
          <Formik
            initialValues={{ isInitialValid: false, title: '', target: '' }}
            validate={values => {
              const errors = {};
              if (!values.title) {
                errors.title = 'Required';
              }
              if (!/^[1-9][0-9]*$/i.test(values.target)) {
                errors.target = 'Invalid number';
              }
              return errors;
            }}
            onSubmit={(values, { setSubmitting }) => {

              this.submit(values);
              setSubmitting(false);
              this.toggle();
            }}
          >
            {({isValid, isSubmitting }) => (
              <Form>
                <h2> New Request </h2>
                <h4> Title </h4>
                <Field label='title' type='title' name='title' placeholder='Enter title' />
                <ErrorMessage name='title' component='div' />
                <h4> Description </h4>
                <Field label='description' component='textarea' type='desc' name='desc' placeholder='Enter description' />
                <ErrorMessage name='desc' component='div' />
                <h4> Target </h4>
                <Field label='target' type='target' name='target' placeholder='Enter target amount' />
                <ErrorMessage name='target' component='div' />
                <button type='submit' className='btn btn-primary' disabled={!isValid || isSubmitting}>
                  Submit
                </button>
                <button type='cancel' className='btn btn-danger' onClick={() => {this.toggle()}}>
                  Cancel
                </button>
              </Form>
            )}
          </Formik>
        </div>
      )
    }
  }
}
