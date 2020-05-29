package sgsits.cse.dis.user.serviceImpl.userProfileServiceImpl;

import org.springframework.stereotype.Service;
import sgsits.cse.dis.user.components.UserProfileRepo;
import sgsits.cse.dis.user.dtos.UserProfileDto;
import sgsits.cse.dis.user.dtos.UserWorkExperienceDto;
import sgsits.cse.dis.user.exception.InternalServerError;
import sgsits.cse.dis.user.model.UserWorkExperience;
import sgsits.cse.dis.user.service.UserProfileService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.*;

@SuppressWarnings("DuplicatedCode")
@Service
public class UserWorkExperienceService implements UserProfileService {

    private final UserProfileRepo userProfileRepo;

    public UserWorkExperienceService(final UserProfileRepo userProfileRepo) {
        this.userProfileRepo = userProfileRepo;
    }


    @Override
    public List<UserProfileDto> getUserProfileElement(final String userId) throws InternalServerError {

        LOGGER.info("Fetching Work Experience info for user id : " + userId);

        final List<UserWorkExperienceDto> userWorkExperienceDtoList =
                userProfileDtoMapper.convertUserWorkExperienceListModelIntoDto(
                userProfileRepo.getUserWorkExperience(userId));

        return new ArrayList<>(userWorkExperienceDtoList);
    }

    public void addUserProfileElement(final UserWorkExperienceDto userWorkExperienceDto,
                                      final String token) throws InternalServerError {

        final UserWorkExperience userWorkExperience =
                userProfileDtoMapper.convertUserWorkExperienceDtoIntoModel(userWorkExperienceDto);

        LOGGER.info("Adding or Updating userElement for id : " + userWorkExperience.getUserId());

        if (0 == userWorkExperience.getId()) {

            userWorkExperience.setCreatedBy(jwtResolver.getIdFromJwtToken(token));
            userWorkExperience.setCreatedDate(new Date(new java.util.Date().getTime()));
        }

        userWorkExperience.setModifiedBy(jwtResolver.getIdFromJwtToken(token));
        userWorkExperience.setModifiedDate(new Date(new java.util.Date().getTime()));

        userWorkExperience.setUserId(jwtResolver.getIdFromJwtToken(token));
        userProfileRepo.addOrUpdateUserWorkExperience(userWorkExperience);
    }

    @Override
    public void deleteUserProfileElementById(final Long id) throws InternalServerError {
        userProfileRepo.deleteUserWorkExperienceById(id);
    }
}
