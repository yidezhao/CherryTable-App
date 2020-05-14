var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var donationSchema = new Schema ({
  id: {type: String, required: true, unique: true},
  donee: {type: String, required: true},
  donor: {type: String, required: true},
  date: Date,
  amount: Number,
  usage: String,
  info: [{
    stage: {
      type: String,
      enum: ['Donor payment confirmed', 'Organization received', 'Shipped','Delivering','Completed']
    },
    date: Date
  }]
});

module.exports = mongoose.model('Donation', donationSchema);
