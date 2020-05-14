// Initialize database
var config = require('./db-config.js');
var uri = 'mongodb+srv://' + config.user + ':' + config.password + '@cherrytable-2rm5d.mongodb.net/test?retryWrites=true&w=majority';
var mongoose = require('mongoose')

mongoose.connect(uri, { useNewUrlParser: true, useUnifiedTopology: true }).then(
  () => { console.log("database is working. successfully connected to Mongo") },
  err => { console.log("not working:" + err.stack) }
);

mongoose.set('useFindAndModify', false);

var User = require('./schemas/User.js');
var Organization = require('./schemas/Organization.js');
var Donation = require('./schemas/Donation.js');
var Request = require('./schemas/Request.js');

// mongoose.connect(uri, {useNewUrlParser: true, useUnifiedTopology: true});
function createDummyLogin(req, res) {
  
  // Create dummy user
  var testUser = new User({
    name: 'test',
    email: 'hello@abc.com',
    passwordHash: '12345',
    contact: '123456789',
    interests: ['cat', 'dog']
  });
  
  
};

function createDummy2(req, res) {
  var ret = {}
  // Create dummy user
  var testUser = new User({
    name: 'dummy_user_1',
    email: 'dummy_user_1@dummyuser.com',
    password: '1234',
    contact: '5555555555',
    interests: ['Hunger', 'Disaster Relief']
  });
  testUser.save((err) => {
    if (err) {
      ret.testUser = err;
    } else {
      ret.testUser = 'success';
    }
    res.json(ret);
  }); 
}

// Route handlers
function createDummy(req, res) {
  var ret = {}
  // Create dummy user
  var testUser = new User({
    name: 'test',
    email: 'hello@abc.com',
    passwordHash: '12345',
    contact: '123456789',
    interests: ['cat', 'dog']
  });
  testUser.save((err) => {
    if (err) {
      ret.testUser = err;
    } else {
      ret.testUser = 'success';
    }
    // Create dummy organization
    var testOrganization = new Organization({
      name: 'Children\'s Hospital of Philadelphia',
      email: 'chp@gmail.com',
      password: 'fighting covid-19',
      address: '3401 Civic Center Blvd, Philadelphia, PA 19104',
      category: 'Medical Aid',
      contact: '2155901000',
      coords: { type: 'Point', coordinates: [39.947797, -75.195450] },
      requests: ["000000", "000001"]
    })
    testOrganization.save((err) => {
      if (err) {
        ret.testOrganization = err;
        console.log(JSON.stringify(ret));
      } else {
        ret.testOrganization = 'success';
      }
      // Create dummy donation
      var testDonation = new Donation({
        id: "000000",
        title: 'fighting covid-19',
        donee: 'Children\'s Hospital of Philadelphia',
        donor: 'tony',
        date: new Date(),
        amount: 10000,
        description: 'Masks for Children\'s Hospital of Philadelphia to fight covid-19',
        info: [{ stage: 'Payment Received', date: new Date() },]
      })
      testDonation.save((err) => {
        if (err) {
          ret.testDonation = err;
        } else {
          res.testDonation = 'success';
        }
        res.json(ret);
      });

      var testDonation2 = new Donation({
        id: "000001",
        title: 'Go Quakers',
        donee: 'UPenn Hospital',
        donor: 'Lucy',
        date: new Date(),
        amount: 10000,
        description: 'Donations for UPenn Hospital',
        info: [{ stage: 'Payment Received', date: new Date() }, { stage: 'Shipped', date: new Date() }]
      })
      testDonation2.save((err) => {
        if (err) {
          ret.testDonation2 = err;
        } else {
          ret.testDonation2 = 'success';
        }
        res.json(ret);
      });

    });
  });
};

function newUser(req, res) {
  res.send("Unimplemented");
}

function getAllRequests(req, res) {

  var organization = req.query.organization;

  Organization.findOne({ name: organization }, (err, organization) => {

    if (err) {
      res.json({ 'status': err });
    } else if (!organization) {

      res.json({ 'status': 'no such organization' });
    } else {

      var requests = [];
      var requestIds = organization.requests;
      //console.log(requestIds);
      Request.find({ _id: { $in: requestIds } }, (err, requests) => {
        if (err) {
          res.json({ 'status': err });

        } else if (!requests) {
          res.json({ 'status': 'no request' });
        } else {
          res.json(requests);
          //console.log(requests);
        }
      });
    }
  });
}

function getAllDonations(req, res) {
  var user_name = 'tony'; // change tony to be the actual user
  Donation.find({ donor: user_name }, (err, donations) => {
    if (err) {
      res.json({ 'status': err });
    } else {
      res.json(donations);
    }
  })
}


function getDonation(req, res) {
  var id = req.query.id;
  Donation.findOne({ _id: id }, (err, donation) => {

    if (err) {
      res.json({ 'status': err });
    } else if (!donation) {

      res.json({ 'status': 'no such donation' });
    } else {

      res.json(donation);
      //console.log(donation);

    }
  });
}


function updateStatus(req, res) {

  var iid = req.query.id;
  var newStatus = req.query.status;

  var newInfo = { "stage": newStatus, "date": new Date() };
  Donation.findOneAndUpdate({ _id: iid }, { $push: { 'info': newInfo } }, { new: true }, (err, donation) => {
    ret = {};
    if (err) {
      ret.orgMessage = err;
    } else if (!donation) {
      ret.orgMessage = 'error: donation does not exist';
    } else {
      ret.orgMessage = 'success: donation status updated';
      ret.organizationInfo = donation;
    }
    res.json(ret);
  })

}


function updateRequest(req, res) {
  console.log("hi updating request");
  var iid = req.query.id;
  var desc = req.query.desc;

  Request.findOneAndUpdate({ _id: iid }, { $set: { 'description': desc } }, { new: true }, (err, request) => {
    ret = {};
    if (err) {
      ret.orgMessage = err;
    } else if (!request) {
      ret.orgMessage = 'error: request does not exist';
    } else {
      ret.orgMessage = 'success: request status updated';
      ret.organizationInfo = request;
    }
    res.json(ret);
  })

}

function newRequest(req, res) {
  var title = req.query.title;
  var desc = req.query.desc;
  var target = req.query.target;
  var organization = req.query.organization;
  if (!title || !desc || !target) {
    res.json({ 'message': 'Incomplete information' });
  }
  if (!organization) {
    organization = 'Childrens Hospital of Philadelphia';
  }
  // var newId = Math.round(Math.random() * 100000).toString();
  var newRequest = new Request({
    title: title,
    description: desc,
    organization: organization,
    target: parseInt(target),
    fulfilled: 0
  });
  newRequest.save((err, request) => {
    ret = {};
    if (err) {
      ret.reqMessage = err;
      res.json(ret);
    } else {
      ret.reqMessage = 'success: submitted new request';
      ret.requestId = request._id;
      // Update the request list of organization
      Organization.findOneAndUpdate({ name: organization },
        { $push: { 'requests': request._id.toString() } },
        { new: true }, (err, org) => {
          if (err) {
            ret.orgMessage = err;
          } else if (!org) {
            ret.orgMessage = 'error: organization does not exist';
          } else {
            ret.orgMessage = 'success: organization requests updated';
            ret.organizationInfo = org;
          }
          res.json(ret);
        })
    }
  })
}

function getCurrentProfile(req, res) {
  var username = req.query.name;
  console.log(username);

  User.findOne({ name: username }, (err, user) => {
    if (err) {
      res.json({ 'error': 'error contacting the database' });
    }
    else if (!user) {
      res.json({ 'status': 'no user with that name' });
    }
    else {
      res.json(
        {
          'status': 'success',
          'user': user
        });
    }
  });
}

function fetchOrgDetails(req, res) {
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000');
  var orgName = req.query.name;

  Organization.findOne( {name : orgName }, (err, org) => {
    if (err) {
      res.json({ 'error': 'error contacting the database' });
    }
    else if (!org) {
      res.json({ 'status': 'no org with that name' });
    }
    else {
      res.json(
        {
          'status': 'success',
          'org': org
        });
    }
  });
  console.log(req.query.name);
}

function updateOrgProfile(req, res) {
  ok = true;
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000');

  filter = { name: req.query.oldName };
  update = {
  name: req.query.name,
  email: req.query.email,
  contact: req.query.phone,
  address: req.query.address,
  category: req.query.category,
  description: req.query.description};

  Organization.findOne( {name : req.query.name }, (err, org) => {
    if (err) {
      res.json({'error' : 'error contacting the database'});
    }
    else if (org) {
      if (org.name != req.query.oldName) {
        res.json({'status' : 'That organization name is taken!'});
        ok = false; // returns for us
      } else {
        let newOrg = Organization.findOneAndUpdate(filter, update,
          {new: true}, (err, org) => {
            if (err) {
              res.json({'error' : 'error contacting the database'});
            } else {
              res.json(
                {'status': 'success',
                'org': org});
            }
          });
      }
    }
  });
}

function deleteProfile(req, res) {
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000');

  username = req.query.name;
  User.deleteOne( {name : username }, (err) => {
    if (err) {
      res.json({'error' : 'error contacting the database'});
    } else {
      res.json({
          'status': 'success',
        });
    }
  });
}

// Uploading images
const AWS = require('aws-sdk');
const fs = require('fs');
const FileType = require('file-type');
const bluebird = require('bluebird');
const multiparty = require('multiparty');

// configure the keys for accessing AWS
AWS.config.update({
  accessKeyId: 'AKIAIPFYJSPPQ4CBYPQQ',
  secretAccessKey: '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'
});

// configure AWS to work with promises
AWS.config.setPromisesDependency(bluebird);

// create S3 instance
const s3 = new AWS.S3();

// abstracts function to upload a file returning a promise
const uploadFile = (buffer, name, type) => {
  const params = {
    ACL: 'public-read',
    Body: buffer,
    Bucket: "cherrytable",
    ContentType: type.mime,
    // ContentType: 'image/jpg',
    Key: `${name}.${type.ext}`
  };
  return s3.upload(params).promise();
};

// Define POST route
function uploadImage(req, res) {
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000');

  const form = new multiparty.Form();

  form.parse(req, async (error, fields, files) => {

    if (error) throw new Error(error);
    try { 
      const path = files.pic[0].path;
      const buffer = fs.readFileSync(path);
      const type = FileType.fromBuffer(buffer);
      const timestamp = Date.now().toString();
      const fileName = `bucketFolder2/${timestamp}-lg`;
      const data = await uploadFile(buffer, fileName, type);
      console.log(data.Location);

      updateOrgProfileWithImage(fields.name[0], data.Location, res);

    } catch (error) {
      console.log(error.stack);
      return res.status(400).send(error);
    }
  });
}

function updateOrgProfileWithImage(currName, url, res) {
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000');

  filter = { name: currName };
  update = {
    profilePic: url
  };

  let newOrg = Organization.findOneAndUpdate(filter, update,
    { new: true }, (err, org) => {
      if (err) {
        res.json({ 'error': 'error contacting the database' });
      } else {
        res.json(
          {
            'status': 'success',
            'org': org
          });
      }
    });
}

function uploadImageGallery(req, res) {
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000');

  const form = new multiparty.Form();

    form.parse(req, async (error, fields, files) => {
      if (error) throw new Error(error);
      try { 

        const path = files.pic[0].path;
        const buffer = fs.readFileSync(path);
        const type = FileType.fromBuffer(buffer);
        const timestamp = Date.now().toString();
        const fileName = `bucketFolder2/${timestamp}-lg`;
        const data = await uploadFile(buffer, fileName, type);
        console.log(data.Location);
        updateOrgProfileWithGalleryImage(fields.name[0], data.Location, res);
      } catch (error) {
        console.log(error.stack);
        return res.status(400).send(error);
      }
    });
}

function updateOrgProfileWithGalleryImage(currName, url, res) {
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000');

  filter = { name: currName };
  update = {
    $push: { 'galleryUrls': url }
  };

  let newOrg = Organization.findOneAndUpdate(filter, update,
    { new: true }, (err, org) => {
      if (err) {
        res.json({ 'error': 'error contacting the database' });
      } else {
        res.json(
          {
            'status': 'success',
            'org': org
          });
      }
    });
}

// Used on the mobile side
function getCurrentProfile(req, res) {
  var username = req.query.name;

  User.findOne( {name : username }, (err,user) => {
    if (err) {
      res.json({'error' : 'error contacting the database'});
    } else if (!user) {
      res.json({'status' : 'no user with that name'});
    } else {
      res.json(
        {
          'status': 'success',
          'user': user
        });
    }
  });
}

// Query parameters: latitute (lat), longitude (long)
function organizationsNearLocation(req, res) {
  var lat = req.query.lat;
  var long = req.query.long;
  if (!lat || !long) {
      res.json({'error': 'Incomplete query parameters'});
  }
  Organization.find(
    {
      coords: {
        $near: {
          $geometry: {
            type: 'Point',
            coordinates: [long, lat]
          },
          $maxDistance: 10000
        }
      }
    },
    (err, orgs) => {
      if (err) {
        res.json(err);
      } else {
        res.json(orgs);
      }
    }
  )
}

function makeDonation(req, res) {
  var donor = req.query.donor;
  var donee = req.query.donee;
  var requestId = req.query.requestId;
  var amount = req.query.amount;
  if (!donor || ! donee || !requestId || !amount) {
    res.json({'error': 'Incomplete query parameters'});
  }

  // 1. create new donation with random id, update donee later
  // 2. update corresponding request (fulfilled, list of donation ids)
  // 3. update donations associated with user
  // 4. return new donation id
  var ret = {};
  var newDonation = new Donation({
    id: Math.round(Math.random() * 100000).toString(),
    donee: donee,
    donor: donor,
    date: new Date(),
    amount: amount,
    info: [{ stage: 'Donor payment confirmed', date: new Date() },]
  })
  newDonation.save((err) => {
    if (err) {
      res.json(err);
    }
    ret.donationId = newDonation.id;
    Request.findOneAndUpdate({ _id: requestId },
                             { $push: { 'donations': newDonation._id },
                               $inc: { 'fulfilled': amount}},
                             { new: true },
                             (err, request) => {
      if (err) {
        res.json(err);
      }
      User.findOneAndUpdate({ name: donor },
                            { $push: { 'donations': newDonation._id } },
                            (err, request) => {
        if (err) {
          res.json(err);
        } else {
          res.json(ret);
        }
      })
    })
  });
}

function fetchDonorsForOrg(req, res) {
  var orgName = req.query.name;
  
  Donation.find( {donee: orgName}, (err, donations) => {
    if (err) {
      res.json({'error' : 'error contacting the database'});
    } else if (!donations || donations.length == 0) {
      res.json({'status' : 'No donations for that organization'});
    } else {
      console.log(donations);
      donorNamesAndAmounts = {};

      donations.forEach(function (item, index) {
        if (item.donor in donorNamesAndAmounts) {
          donorNamesAndAmounts[item.donor] = donorNamesAndAmounts[item.donor] + item.amount;
        } else {
          donorNamesAndAmounts[item.donor] = item.amount;
        }
      });

      x = sortMap(donorNamesAndAmounts);
      console.log(x);

      x.forEach(function (item, index) {
        User.findOne( {name : item[0] }, (err,user) => {
          if (err) {
            res.json({'error' : 'error contacting the database'});
          } else if (user) {
            x[index].push(user.email);
          }
          if (index == x.length - 1) {res.json(
            {'status': 'success',
            'donorArr': x});}
        });
      });
    }
  });
  
}

function sortMap(obj) {
	var result = [];
	for (var key in obj)
    result.push([key, obj[key]]);
	
	// sort items by value
	result.sort(function(a, b) {
		var fst = a[1];
		var snd = b[1];
		return fst < snd ? 11 : fst > snd ? -1 : 0;
	});
	return result; // array in format [ [ key1, val1 ], [ key2, val2 ], ... ]
}

function updateUserProfile(req, res) {
  ok = true;
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000');

  x = req.query.interests.split(",");
  console.log(x);

  filter = { name: req.query.name };
  update = {
  name: req.query.name,
  email: req.query.email,
  contact: req.query.phone,
  $set: { interests: x } };
  // $addToSet: { interests: {$each: x} } };
  // $set: {interests: []}}; // used to clear the array when testing

  User.findOne( {name : req.query.name }, (err, user) => {
    if (err) {
      res.json({'error' : 'error contacting the database'});
    }
    else if (user) {
        let newUser = User.findOneAndUpdate(filter, update,
          {new: true}, (err, user) => {
            if (err) {
              res.json({'error' : 'error contacting the database'});
              console.log(err);
            } else {
              res.json(
                {'status': 'success',
                'user': user});
            }
          });
    }
  });
}

function uploadImageMobileUser(req, res) {
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000');

  const form = new multiparty.Form();

  form.parse(req, async (error, fields, files) => {

    if (error) throw new Error(error);
    try { 
      const path = files.pic[0].path;

      const buffer = fs.readFileSync(path);
      const type = FileType.fromBuffer(buffer);
      const timestamp = Date.now().toString();
      const fileName = `bucketFolder2/${timestamp}-lg`;

      const data = await uploadFile(buffer, fileName, type);
      console.log(data.Location);

      updateUserProfileWithImage(fields.name[0], data.Location, res);

    } catch (error) {
      console.log(error.stack);
      return res.status(400).send(error);
    }
  });
}

function updateUserProfileWithImage(currName, url, res) {
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:3000');

  filter = { name: currName };
  update = {
    profilePic: url
  };

  let newOrg = User.findOneAndUpdate(filter, update,
    { new: true }, (err, user) => {
      if (err) {
        res.json({ 'error': 'error contacting the database' });
      } else {
        res.json(
          {
            'status': 'success',
            'user': user
          });
      }
    });
}

module.exports = {
  createDummyLogin:createDummyLogin,
  createDummy: createDummy,
  createDummy2: createDummy2,
  newUser: newUser,
  newRequest: newRequest,
  getCurrentProfile: getCurrentProfile,
  fetchOrgDetails: fetchOrgDetails,
  updateOrgProfile: updateOrgProfile,
  uploadImage: uploadImage,
  uploadImageGallery: uploadImageGallery,
  getAllRequests: getAllRequests,
  updateRequest: updateRequest,
  getDonation: getDonation,
  updateStatus: updateStatus,
  getAllDonations: getAllDonations,
  organizationsNearLocation: organizationsNearLocation,
  makeDonation: makeDonation,
  fetchDonorsForOrg: fetchDonorsForOrg,
  updateUserProfile: updateUserProfile,
  uploadImageMobileUser: uploadImageMobileUser,
  deleteProfile: deleteProfile
}