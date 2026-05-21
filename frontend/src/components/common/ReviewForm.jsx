import React, { useState } from "react";
import axios from "axios";

const ReviewForm = () => {

  const [rating, setRating] = useState("");
  const [comment, setComment] = useState("");

  const submitReview = async () => {

    const reviewData = {
      rating: rating,
      comment: comment,
      routeId: 1,
      userName: "Shruti"
    };

    try {

      await axios.post(
        "http://localhost:8080/api/reviews",
        reviewData
      );

      alert("Review Added Successfully");

      setRating("");
      setComment("");

    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div>

      <h2>Give Review</h2>

      <input
        type="number"
        placeholder="Rating 1 to 5"
        value={rating}
        onChange={(e) => setRating(e.target.value)}
      />

      <br /><br />

      <textarea
        placeholder="Write comment"
        value={comment}
        onChange={(e) => setComment(e.target.value)}
      />

      <br /><br />

      <button onClick={submitReview}>
        Submit Review
      </button>

    </div>
  );
};

export default ReviewForm;