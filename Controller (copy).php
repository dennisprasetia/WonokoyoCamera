<?php
//header('Access-Control-Allow-Origin: *');
ini_set('display_errors', 1); // 0 No show errors, 1

require_once 'controller/LoginController.php';
require_once 'controller/EntryRhkController.php';
// require_once 'controller/FingerController.php';

function doAction($action, $params = null)
{
	$response = ['status' => 0, 'message' => null, 'content' => null];

	switch ($action) {
		case 'upload_video':
			try {
				$file_path = basename($_FILES['video']['name']);
				if (move_uploaded_file($_FILES['video']['tmp_name'], 'upload/video' . $file_path)) {
					$response['message'] = 'Video successfully uploaded';
				} else {
					$response['message'] = 'Error uploading video';
				}
			} catch (Exception $exc) {
				$response['message'] = $exc->getTraceAsString();
			}
			break;

		case 'upload_photo':
			try {
				$file_path = basename($_FILES['photo']['name']);
				if (move_uploaded_file($_FILES['photo']['tmp_name'], 'upload/photo' . $file_path)) {
					$response['message'] = 'Photo successfully uploaded';
				} else {
					$response['message'] = 'Error uploading video';
				}
			} catch (Exception $exc) {
				$response['message'] = $exc->getTraceAsString();
			}
			break;

		default:
			// do nothing
			break;
	}
	return $response;
}